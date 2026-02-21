package com.example.common.result;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 分页查询参数
 */
@Data
@Accessors(chain = true)
public class PageQuery {

    // 页码
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = 1;

    // 每页查询数量
    @Min(value = 1, message = "每页查询数量不能小于1")
    private Integer pageSize = 10;

    // 是否升序
    private Boolean isAsc = true;

    // 排序方式
    private String sortBy;

    /**
     * 计算分页查询的起始索引
     */
    public int from() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 将分页查询参数转换为MyBatis-Plus的分页对象
     *
     * @param orderItems 排序字段数组
     */
    public <T> Page<T> toMpPage(OrderItem... orderItems) {
        Page<T> page = new Page<>(pageNo, pageSize);
        // 是否手动指定排序方式
        if (orderItems != null && orderItems.length > 0) {
            for (OrderItem orderItem : orderItems) {
                page.addOrder(orderItem);
            }
            return page;
        }
        // 前端是否有排序字段
        if (StringUtils.isNotBlank(sortBy)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(isAsc);
            orderItem.setColumn(sortBy);
            page.addOrder(orderItem);
        }
        return page;
    }

    /**
     * 将分页查询参数转换为MyBatis-Plus的分页对象
     *
     * @param defaultSortBy 默认排序字段
     * @param isAsc         默认排序方式
     */
    public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc) {
        if (StringUtils.isBlank(sortBy)) {
            sortBy = defaultSortBy;
            this.isAsc = isAsc;
        }
        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem();
        orderItem.setAsc(this.isAsc);
        orderItem.setColumn(sortBy);
        page.addOrder(orderItem);
        return page;
    }

    /**
     * 将分页查询参数转换为MyBatis-Plus的分页对象，默认排序字段为create_time，默认排序方式为降序
     */
    public <T> Page<T> toMpPageDefaultSortByCreateTimeDesc() {
        return toMpPage("create_time", false);
    }
}
