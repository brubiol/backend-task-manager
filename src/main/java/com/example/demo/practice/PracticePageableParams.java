package com.example.demo.practice;

/**
 * Practice helper for pageable and filter parameters.
 */
public class PracticePageableParams {

    private Integer page = 0;
    private Integer size = 20;
    private String sort = "id,asc";

    private String status;
    private String assignee;

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
}
