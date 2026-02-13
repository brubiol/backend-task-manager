#!/usr/bin/env bash
set -euo pipefail

# ============================================================
# deploy.sh — Build, push to ECR, and deploy to ECS Fargate
#
# Usage:
#   ./deploy.sh                          # uses defaults
#   AWS_REGION=us-west-2 ./deploy.sh     # override region
# ============================================================

AWS_REGION="${AWS_REGION:-us-east-1}"
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REPO="task-manager"
IMAGE_TAG="${IMAGE_TAG:-latest}"
IMAGE_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}"
STACK_NAME="${STACK_NAME:-task-manager-prod}"
CLUSTER_NAME="prod-cluster"
SERVICE_NAME=""  # resolved from CloudFormation after deploy

echo "==> AWS Account: ${AWS_ACCOUNT_ID}"
echo "==> Region:      ${AWS_REGION}"
echo "==> Image:       ${IMAGE_URI}"

# ---- 1. Create ECR repo if it doesn't exist ----
echo "==> Ensuring ECR repository exists..."
aws ecr describe-repositories --repository-names "${ECR_REPO}" --region "${AWS_REGION}" 2>/dev/null \
  || aws ecr create-repository --repository-name "${ECR_REPO}" --region "${AWS_REGION}"

# ---- 2. Docker login to ECR ----
echo "==> Logging in to ECR..."
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# ---- 3. Build and push ----
echo "==> Building Docker image..."
docker build -t "${ECR_REPO}:${IMAGE_TAG}" .

echo "==> Tagging and pushing..."
docker tag "${ECR_REPO}:${IMAGE_TAG}" "${IMAGE_URI}"
docker push "${IMAGE_URI}"

# ---- 4. Deploy CloudFormation stack ----
echo "==> Deploying CloudFormation stack: ${STACK_NAME}"
echo "    (If this is the first deploy, you'll be prompted for DB password and JWT secret)"

aws cloudformation deploy \
  --template-file infra/template.yml \
  --stack-name "${STACK_NAME}" \
  --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    ImageUri="${IMAGE_URI}" \
  --region "${AWS_REGION}" \
  --no-fail-on-empty-changeset

# ---- 5. Force new deployment (picks up latest image) ----
echo "==> Forcing new ECS deployment..."
SERVICE_ARN=$(aws ecs list-services --cluster "${CLUSTER_NAME}" --region "${AWS_REGION}" --query 'serviceArns[0]' --output text)
aws ecs update-service \
  --cluster "${CLUSTER_NAME}" \
  --service "${SERVICE_ARN}" \
  --force-new-deployment \
  --region "${AWS_REGION}" > /dev/null

# ---- 6. Print ALB URL ----
ALB_URL=$(aws cloudformation describe-stacks \
  --stack-name "${STACK_NAME}" \
  --region "${AWS_REGION}" \
  --query 'Stacks[0].Outputs[?OutputKey==`ALBURL`].OutputValue' \
  --output text)

echo ""
echo "==> Deploy complete!"
echo "==> API URL: ${ALB_URL}"
