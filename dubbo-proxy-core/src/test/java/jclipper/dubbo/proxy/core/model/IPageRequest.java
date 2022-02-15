package jclipper.dubbo.proxy.core.model;


/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/10/26 11:08.
 */
public interface IPageRequest {
    /**
     * 当前页码
     *
     * @return
     */
    Integer getCurPage();


    /**
     * 每页数据条数
     *
     * @return
     */
    Integer getPageSize();

    /**
     * 当前偏移量
     * 当直接设置 offset 时，使用该 offset 值,而不从 curPage 和 pageSize 来计算
     *
     * @return
     */
    Integer getOffset();

    default void transferTo(Page<?> page) {
        page.setCurPage(this.getCurPage() == null ? Page.DFT_CUR_PAGE : this.getCurPage());
        page.setPageSize(this.getPageSize() == null ? Page.DFT_PAGE_SIZE : this.getPageSize());
        if (this.getOffset() != null) {
            page.offset(this.getOffset());
        }
    }
}
