package com.kit.maximus.freshskinweb.repository.search;

import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
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
public class BlogSearchRepository {

    OpenSearchClient openSearchClient;

    public void indexBlog(BlogResponse blogResponse) {
        try {
            IndexRequest<BlogResponse> request = new IndexRequest.Builder<BlogResponse>()
                    .index("blogs") // Tên index
                    .id(String.valueOf(blogResponse.getId())) // ID sản phẩm
                    .document(blogResponse) // Dữ liệu sản phẩm
                    .build();

            openSearchClient.index(request);
            System.out.println("Indexed Blogs: " + blogResponse.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean deleteBlogs(Long id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest.Builder()
                    .index("blogs")
                    .id(String.valueOf(id))
                    .build();


            DeleteResponse deleteResponse = openSearchClient.delete(deleteRequest);


            if ("deleted".equals(deleteResponse.result().toString())) {
                log.info("Blogs with ID {} was deleted. Result: {}", id, deleteResponse.result());
                return true;
            } else {
                log.warn("Failed to delete Blogs with ID {}. Result: {}", id, deleteResponse.result());
                return false;
            }
        } catch (IOException e) {
            // Log lỗi khi gặp sự cố trong quá trình xóa
            log.error("Error while deleting product with ID {}", id, e);
            return false;
        }
    }


    public BlogResponse searchBySlug(String slug) {
        try {
            TermQuery termQuery = new TermQuery.Builder()
                    .field("slug.keyword")
                    .value(FieldValue.of(slug))
                    .build();

            Query searchQuery = new Query.Builder()
                    .term(termQuery)
                    .build();


            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogs")
                    .query(searchQuery)
                    .size(1)
                    .build();

            // Gửi yêu cầu tìm kiếm
            SearchResponse<BlogResponse> response = openSearchClient.search(searchRequest, BlogResponse.class);

            // Kiểm tra kết quả và trả về sản phẩm tìm thấy
            if (!response.hits().hits().isEmpty()) {
                return response.hits().hits().getFirst().source();
            } else {
                return null; // Không tìm thấy sản phẩm
            }

        } catch (IOException e) {
            log.error("Error while fetching BlogResponse by slug", e);
            return null; // Trả về null nếu gặp lỗi
        }
    }


    public List<BlogResponse> getBlogsByCategorySlug(String categorySlug, String status, boolean deleted, int page, int size) {
        try {
            // Xây dựng query
            Query query = new Query.Builder()
                    .bool(b -> b
                            .must(m -> m.term(t -> t
                                    .field("blogCategory.slug.keyword") // Sử dụng slug thay vì ID
                                    .value(FieldValue.of(categorySlug))
                            ))
                            .filter(f -> f.term(t -> t
                                    .field("status.keyword")
                                    .value(FieldValue.of(status))
                            ))
                            .filter(f -> f.term(t -> t
                                    .field("deleted")
                                    .value(FieldValue.of(deleted))
                            ))
                    )
                    .build();

            // Tạo request
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogs") // Index Elasticsearch
                    .query(query)
                    .from(page * size)
                    .size(size)
                    .build();

            // Gửi request đến OpenSearch
            SearchResponse<BlogResponse> response = openSearchClient.search(searchRequest, BlogResponse.class);

            // Trả về danh sách BlogResponse
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error while fetching blogs by categorySlug", e);
            return Collections.emptyList();
        }
    }

    public List<BlogResponse> getBlogsByCategorySlug(String categorySlug, String status, boolean deleted) {
        try {
            // Xây dựng query
            Query query = new Query.Builder()
                    .bool(b -> b
                            .must(m -> m.term(t -> t
                                    .field("blogCategory.slug.keyword") // Sử dụng slug thay vì ID
                                    .value(FieldValue.of(categorySlug))
                            ))
                            .filter(f -> f.term(t -> t
                                    .field("status.keyword")
                                    .value(FieldValue.of(status))
                            ))
                            .filter(f -> f.term(t -> t
                                    .field("deleted")
                                    .value(FieldValue.of(deleted))
                            ))
                    )
                    .build();

            // Tạo request
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogs") // Index Elasticsearch
                    .query(query)
                    .size(300)
                    .build();

            // Gửi request đến OpenSearch
            SearchResponse<BlogResponse> response = openSearchClient.search(searchRequest, BlogResponse.class);

            // Trả về danh sách BlogResponse
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error while fetching blogs by categorySlug", e);
            return Collections.emptyList();
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
            BlogResponse updateFields = new BlogResponse();
            updateFields.setDeleted(deleted);

            UpdateRequest<BlogResponse, BlogResponse> updateRequest =
                    new UpdateRequest.Builder<BlogResponse, BlogResponse>()
                            .index("blogs")
                            .id(String.valueOf(id))
                            .doc(updateFields)
                            .retryOnConflict(3)
                            .build();

            // Thực hiện update
            openSearchClient.update(updateRequest, BlogResponse.class);

        } catch (IOException e) {
            log.error("Error while updating blogs: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while updating blogs: {}", e.getMessage(), e);
        }
    }

    public void update(Long id, String status) {
        try {
            if (id == null) {
                log.error("Invalid input: blogsID is null/empty");
                return;
            }

            log.debug("Updating blogs with ID: {}, deleted: {}", id, status);

            // Chỉ cập nhật field deleted
            BlogResponse updateFields = new BlogResponse();
            updateFields.setStatus(status);

            UpdateRequest<BlogResponse, BlogResponse> updateRequest =
                    new UpdateRequest.Builder<BlogResponse, BlogResponse>()
                            .index("blogs")
                            .id(String.valueOf(id))
                            .doc(updateFields)
                            .retryOnConflict(3)
                            .build();

            // Thực hiện update
            openSearchClient.update(updateRequest, BlogResponse.class);

        } catch (IOException e) {
            log.error("Error while updating blogs: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while updating blogs: {}", e.getMessage(), e);
        }
    }

    public void update(Long id, int position) {
        try {
            if (id == null) {
                log.error("Invalid input: blogsID is null/empty");
                return;
            }

            log.debug("Updating blogs with ID: {}, deleted: {}", id, position);

            // Chỉ cập nhật field deleted
            BlogResponse updateFields = new BlogResponse();
            updateFields.setPosition(position);

            UpdateRequest<BlogResponse, BlogResponse> updateRequest =
                    new UpdateRequest.Builder<BlogResponse, BlogResponse>()
                            .index("blogs")
                            .id(String.valueOf(id))
                            .doc(updateFields)
                            .retryOnConflict(3)
                            .build();

            // Thực hiện update
            openSearchClient.update(updateRequest, BlogResponse.class);

        } catch (IOException e) {
            log.error("Error while updating blogs: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while updating blogs: {}", e.getMessage(), e);
        }
    }

    public boolean update(BlogResponse blogResponse) {
        try {
            // Tạo UpdateRequest để cập nhật sản phẩm với kiểu dữ liệu rõ ràng
            log.debug("Updating product with data: {}", blogResponse);
            UpdateRequest<BlogResponse, BlogResponse> updateRequest = new UpdateRequest.Builder<BlogResponse, BlogResponse>()
                    .index("blogs")
                    .id(String.valueOf(blogResponse.getId()))
                    .doc(blogResponse)
                    .retryOnConflict(3)
                    .build();

            // Thực hiện yêu cầu cập nhật
            UpdateResponse<BlogResponse> updateResponse = openSearchClient.update(updateRequest, BlogResponse.class);

            InlineGet<BlogResponse> get = updateResponse.get();
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


    public List<BlogResponse> showAll() {
        try {
            Query searchQuery = new Query.Builder()
                    .matchAll(new MatchAllQuery.Builder().build()) // match_all để lấy tất cả các bản ghi
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogs")
                    .query(searchQuery)
                    .size(300) // Chỉ định số lượng kết quả tối đa cần trả về
                    .build();

            // Gửi yêu cầu tìm kiếm
            SearchResponse<BlogResponse> response = openSearchClient.search(searchRequest, BlogResponse.class);

            // Kiểm tra kết quả và trả về các bản ghi tìm thấy
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error while fetching all blog posts", e);
            return Collections.emptyList(); // Trả về danh sách rỗng nếu gặp lỗi
        }
    }


    public List<BlogResponse> showAll(String status, boolean deleted) {
        try {
            Query searchQuery = new Query.Builder()
                    .bool(b -> b
                            .filter(new Query.Builder()
                                    .term(t -> t.field("status.keyword").value(FieldValue.of(status)))
                                    .build()
                            )
                            .filter(new Query.Builder()
                                    .term(t -> t.field("deleted").value(FieldValue.of(deleted)))
                                    .build()
                            )
                    )
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogs")
                    .query(searchQuery)
                    .size(300)
                    .build();

            SearchResponse<BlogResponse> response = openSearchClient.search(searchRequest, BlogResponse.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error while fetching all blogcategory posts", e);
            return Collections.emptyList();
        }
    }


}
