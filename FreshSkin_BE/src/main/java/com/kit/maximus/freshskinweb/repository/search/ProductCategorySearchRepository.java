package com.kit.maximus.freshskinweb.repository.search;

import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.MatchAllQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCategorySearchRepository {


    OpenSearchClient openSearchClient;

    public void indexProductCategory(ProductCategoryResponse productCategoryResponse) {
        try {
            IndexRequest<ProductCategoryResponse> request = new IndexRequest.Builder<ProductCategoryResponse>()
                    .index("productcategory") // Tên index
                    .id(String.valueOf(productCategoryResponse.getId())) // ID sản phẩm
                    .document(productCategoryResponse) // Dữ liệu sản phẩm
                    .build();

            openSearchClient.index(request);
            System.out.println("Indexed productcategory: " + productCategoryResponse.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(Long id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest.Builder()
                    .index("productcategory")
                    .id(String.valueOf(id))
                    .build();

            DeleteResponse deleteResponse = openSearchClient.delete(deleteRequest);


            if ("deleted".equals(deleteResponse.result().toString())) {
                log.info("Blogs with ID {} was deleted. Result: {}", id, deleteResponse.result());
                return true;
            } else {
                log.warn("Failed to delete ProductCategory with ID {}. Result: {}", id, deleteResponse.result());
                return false;
            }
        } catch (IOException e) {
            log.error("Error while deleting ProductCategory with ID {}", id, e);
            return false;
        }
    }

    public List<ProductCategoryResponse> showAll() {
        try {
            Query searchQuery = new Query.Builder()
                    .matchAll(new MatchAllQuery.Builder().build()) // match_all để lấy tất cả các bản ghi
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("productcategory")
                    .query(searchQuery)
                    .size(300) // Chỉ định số lượng kết quả tối đa cần trả về
                    .build();

            // Gửi yêu cầu tìm kiếm
            SearchResponse<ProductCategoryResponse> response = openSearchClient.search(searchRequest, ProductCategoryResponse.class);

            // Kiểm tra kết quả và trả về các bản ghi tìm thấy
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error while fetching all productCategory posts", e);
            return Collections.emptyList(); // Trả về danh sách rỗng nếu gặp lỗi
        }
    }

    public List<ProductCategoryResponse> showAll(String status, boolean deleted) {
        try {
            Query searchQuery = new Query.Builder()
                    .bool(b -> b
                            .filter(new Query.Builder()
                                    .term(t -> t.field("status.keyword").value(FieldValue.of(status))) // Lọc theo status = ACTIVE
                                    .build()
                            )
                            .filter(new Query.Builder()
                                    .term(t -> t.field("deleted").value(FieldValue.of(deleted))) // Lọc theo deleted = false
                                    .build()
                            )
                    )
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("productcategory")
                    .query(searchQuery)
                    .size(300)
                    .build();

            SearchResponse<ProductCategoryResponse> response = openSearchClient.search(searchRequest, ProductCategoryResponse.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error while fetching all productCategory posts", e);
            return Collections.emptyList();
        }
    }


}
