package com.kit.maximus.freshskinweb.repository.search;

import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.InlineGet;
import org.opensearch.client.opensearch._types.query_dsl.*;
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
public class BlogCategorySearchRepository {

    OpenSearchClient openSearchClient;

    public void indexBlogCategory(BlogCategoryResponse blogResponse) {
        try {
            IndexRequest<BlogCategoryResponse> request = new IndexRequest.Builder<BlogCategoryResponse>()
                    .index("blogcategory") // Tên index
                    .id(String.valueOf(blogResponse.getId())) // ID sản phẩm
                    .document(blogResponse) // Dữ liệu sản phẩm
                    .build();

            openSearchClient.index(request);
            System.out.println("Indexed blogcategory: " + blogResponse.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Long id, boolean deleted) {
        try {
            if (id == null) {
                log.error("Invalid input: blogsID is null/empty");
                return;
            }

            log.debug("Updating blogs with ID: {}, deleted: {}", id, deleted);

            // Chỉ cập nhật field deleted
            BlogCategoryResponse updateFields = new BlogCategoryResponse();
            updateFields.setDeleted(deleted);

            UpdateRequest<BlogCategoryResponse, BlogCategoryResponse> updateRequest =
                    new UpdateRequest.Builder<BlogCategoryResponse, BlogCategoryResponse>()
                            .index("blogcategory")
                            .id(String.valueOf(id))
                            .doc(updateFields)
                            .retryOnConflict(3)
                            .build();

            // Thực hiện update
            openSearchClient.update(updateRequest, BlogCategoryResponse.class);

        } catch (IOException e) {
            log.error("Error while updating blogs: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while updating blogs: {}", e.getMessage(), e);
        }
    }

    public void update(Long id, String status) {
        try {
            if (id == null) {
                log.error("Invalid input: blogsCategoryID is null/empty");
                return;
            }

            log.debug("Updating blogsCategory with ID: {}, deleted: {}", id, status);

            // Chỉ cập nhật field deleted
            BlogCategoryResponse updateFields = new BlogCategoryResponse();
            updateFields.setStatus(status);

            UpdateRequest<BlogCategoryResponse, BlogCategoryResponse> updateRequest =
                    new UpdateRequest.Builder<BlogCategoryResponse, BlogCategoryResponse>()
                            .index("blogcategory")
                            .id(String.valueOf(id))
                            .doc(updateFields)
                            .retryOnConflict(3)
                            .build();

            // Thực hiện update
            openSearchClient.update(updateRequest, BlogCategoryResponse.class);

        } catch (IOException e) {
            log.error("Error while updating blogs: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while updating blogs: {}", e.getMessage(), e);
        }
    }

    public void update(Long id, int position) {
        try {
            if (id == null) {
                log.error("Invalid input: blogsCategoryID is null/empty");
                return;
            }

            log.debug("Updating blog-category with ID: {}, deleted: {}", id, position);

            // Chỉ cập nhật field deleted
            BlogCategoryResponse updateFields = new BlogCategoryResponse();
            updateFields.setPosition(position);

            UpdateRequest<BlogCategoryResponse, BlogCategoryResponse> updateRequest =
                    new UpdateRequest.Builder<BlogCategoryResponse, BlogCategoryResponse>()
                            .index("blogcategory")
                            .id(String.valueOf(id))
                            .doc(updateFields)
                            .retryOnConflict(3)
                            .build();

            // Thực hiện update
            openSearchClient.update(updateRequest, BlogCategoryResponse.class);

        } catch (IOException e) {
            log.error("Error while updating blogs: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while updating blogs: {}", e.getMessage(), e);
        }
    }

    public boolean update(BlogCategoryResponse blogCategoryResponse) {
        try {
            // Tạo UpdateRequest để cập nhật sản phẩm với kiểu dữ liệu rõ ràng
            log.debug("Updating blog-category with data: {}", blogCategoryResponse);
            UpdateRequest<BlogCategoryResponse, BlogCategoryResponse> updateRequest = new UpdateRequest.Builder<BlogCategoryResponse, BlogCategoryResponse>()
                    .index("blogcategory")
                    .id(String.valueOf(blogCategoryResponse.getId()))
                    .doc(blogCategoryResponse)
                    .retryOnConflict(3)
                    .build();

            // Thực hiện yêu cầu cập nhật
            UpdateResponse<BlogCategoryResponse> updateResponse = openSearchClient.update(updateRequest, BlogCategoryResponse.class);

            InlineGet<BlogCategoryResponse> get = updateResponse.get();
            if (get != null && get.source() != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            log.error("Error while updating product", e);
            return false;
        }
    }


    public boolean deleteBlogCategory(Long id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest.Builder()
                    .index("blogcategory")
                    .id(String.valueOf(id))
                    .build();


            DeleteResponse deleteResponse = openSearchClient.delete(deleteRequest);


            if ("deleted".equals(deleteResponse.result().toString())) {
                log.info("BlogsCategory with ID {} was deleted. Result: {}", id, deleteResponse.result());
                return true;
            } else {
                log.warn("Failed to delete BlogsCategory with ID {}. Result: {}", id, deleteResponse.result());
                return false;
            }
        } catch (IOException e) {
            // Log lỗi khi gặp sự cố trong quá trình xóa
            log.error("Error while deleting BlogsCategory with ID {}", id, e);
            return false;
        }
    }



    public List<BlogCategoryResponse> showAll() {
        try {
            Query searchQuery = new Query.Builder()
                    .matchAll(new MatchAllQuery.Builder().build()) // match_all để lấy tất cả các bản ghi
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogcategory")
                    .query(searchQuery)
                    .size(300) // Chỉ định số lượng kết quả tối đa cần trả về
                    .build();

            // Gửi yêu cầu tìm kiếm
            SearchResponse<BlogCategoryResponse> response = openSearchClient.search(searchRequest, BlogCategoryResponse.class);

            // Kiểm tra kết quả và trả về các bản ghi tìm thấy
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error while fetching all blogcategory posts", e);
            return Collections.emptyList(); // Trả về danh sách rỗng nếu gặp lỗi
        }
    }

    public List<BlogCategoryResponse> showAll(String status, boolean deleted) {
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
                    .index("blogcategory")
                    .query(searchQuery)
                    .size(300)
                    .build();

            SearchResponse<BlogCategoryResponse> response = openSearchClient.search(searchRequest, BlogCategoryResponse.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error while fetching all blogcategory posts", e);
            return Collections.emptyList();
        }
    }


}

