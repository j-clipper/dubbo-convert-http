package jclipper.dubbo.proxy.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 描述:分页对象
 *
 * @author WQB
 * @created 2014-5-21 上午9:29:16
 * @since v1.0.0
 */
@ApiModel("分页封装类")
public class Page<T> implements Serializable {

    public static final int OFFSET_IGNORE = -1;
    public static final int DFT_CUR_PAGE = 1;
    public static final int DFT_PAGE_SIZE = 10;
    public static final int DFT_PAGE_COUNT = 10;

    public static final int NO_ROW_OFFSET = 0;
    public static final int NO_ROW_LIMIT = 2147483647;

    private int limit;

    /**
     * 当前页码
     */
    @ApiModelProperty(value = "当前页码")
    private int curPage = DFT_CUR_PAGE;
    /**
     * 每页数据条数
     */
    @ApiModelProperty(value = "每页数据条数")
    private int pageSize = DFT_PAGE_SIZE;
    /**
     * 数据总条数
     */
    @ApiModelProperty(value = "数据总条数")
    private int totalSize;
    /**
     * 数据总页数
     */
    @ApiModelProperty(value = "数据总页数")
    private int totalPage;

    /**
     * 当直接设置 offset 时，使用该 offset 值,而不从 curPage 和 pageSize 来计算
     */
    private int offset = OFFSET_IGNORE;

    private List<T> dataList = Collections.emptyList();

    /**
     * 分页显示的页数
     */
    private int pageCount = DFT_PAGE_COUNT;

    public Page() {
        this(DFT_CUR_PAGE, DFT_PAGE_SIZE);
    }

    public Page(int curPage, int pageSize) {
        this.curPage = curPage;
        this.pageSize = pageSize;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage < DFT_CUR_PAGE ? DFT_CUR_PAGE : curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? DFT_PAGE_SIZE : pageSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;

        if (0 != totalSize) {
            totalPage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
            this.curPage = this.curPage > totalPage ? totalPage : curPage;
        }
    }

    public int getTotalPage() {
        return totalPage;
    }

    /**
     * 获取 offset
     */
    public int getOffset() {
        if (offset >= 0) {
            return offset;
        } else {
            return curPage > 0 ? (curPage - 1) * pageSize : 0;
        }
    }

    /**
     * 设置 offset
     *
     * @param val
     * @return 当前对象
     */
    public Page offset(int val) {
        this.offset = val;
        return this;
    }

    /**
     * 获取 limit
     */
    public int getLimit() {
        return pageSize;
    }

    /**
     * 设置 limit
     *
     * @param val
     * @return
     */
    public Page limit(int val) {
        setPageSize(val);
        return this;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    /**
     * @return the pageCount
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * @param pageCount the pageCount to set
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount <= 0 ? DFT_PAGE_COUNT : pageCount;
    }

    public int getStartPage() {
        int endPage = getEndPage();
        int startPage = endPage - pageCount + 1;
        if (startPage < 1) {
            startPage = 1;
        }
        return startPage;
    }

    public int getEndPage() {
        int endPage = curPage + pageCount / 2 - 1;
        if (endPage < pageCount) {
            endPage = pageCount;
        }
        if (endPage > totalPage) {
            endPage = totalPage;
        }
        return endPage;
    }

}
