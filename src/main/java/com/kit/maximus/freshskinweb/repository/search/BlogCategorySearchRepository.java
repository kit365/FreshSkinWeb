package com.kit.maximus.freshskinweb.repository.search;

import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
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

    public BlogCategoryResponse getBlogstById(Long id) {
        try {
            GetRequest getRequest = new GetRequest.Builder()
                    .index("blogcategory")
                    .id(String.valueOf(id))
                    .build();

            GetResponse<BlogCategoryResponse> getResponse = openSearchClient.get(getRequest, BlogCategoryResponse.class);

            if (getResponse.found()) {
                return getResponse.source();
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error while fetching blogcategory by ID", e);
            return null;
        }
    }


    public boolean deleteBlogs(Long id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest.Builder()
                    .index("blogcategory")
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


    public List<BlogCategoryResponse> searchByTitle(String title, int size) {
        try {

            MatchQuery matchQuery = new MatchQuery.Builder()
                    .field("title") // Chỉ định trường "title"
                    .query(FieldValue.of(title))
                    .fuzziness("auto")
                    .operator(Operator.And) // Cấu hình để tất cả các từ đều phải xuất hiện
                    .build();

            // Xây dựng truy vấn với MatchQuery
            Query searchQuery = new Query.Builder()
                    .match(matchQuery)
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogcategory") // Chỉ định index
                    .query(searchQuery) // Thêm truy vấn vào request
                    .size(size) // Số lượng kết quả trả về
                    .build();

            // Gửi yêu cầu tìm kiếm
            SearchResponse<BlogCategoryResponse> response = openSearchClient.search(searchRequest, BlogCategoryResponse.class);


            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Lỗi khi tìm kiếm sản phẩm trên OpenSearch", e);
            return Collections.emptyList();
        }
    }

    public BlogCategoryResponse searchBySlug(String slug) {
        try {
            TermQuery termQuery = new TermQuery.Builder()
                    .field("slug.keyword")
                    .value(FieldValue.of(slug))
                    .build();

            Query searchQuery = new Query.Builder()
                    .term(termQuery)
                    .build();


            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogcategory")
                    .query(searchQuery)
                    .size(1)
                    .build();

            // Gửi yêu cầu tìm kiếm
            SearchResponse<BlogCategoryResponse> response = openSearchClient.search(searchRequest, BlogCategoryResponse.class);

            // Kiểm tra kết quả và trả về sản phẩm tìm thấy
            if (!response.hits().hits().isEmpty()) {
                return response.hits().hits().getFirst().source();
            } else {
                return null; // Không tìm thấy sản phẩm
            }

        } catch (IOException e) {
            log.error("Error while fetching blogcategory by slug", e);
            return null; // Trả về null nếu gặp lỗi
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

    public List<Long> getBlogCategoryIds(String status, boolean deleted) {
        try {
            Query query = new Query.Builder()
                    .bool(b -> b
                            .must(m -> m
                                    .matchAll(new MatchAllQuery.Builder().build())
                            )
                            .filter(f -> f
                                    .term(t -> t.field("status.keyword").value(FieldValue.of(status)))
                            )
                            .filter(f -> f
                                    .term(t -> t.field("deleted").value(FieldValue.of(deleted)))
                            )
                    )
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("blogcategory")
                    .query(query)
                    .source(s -> s
                            .filter(f -> f.includes("id")) // Chỉ lấy field "id"
                    )
                    .size(30)
                    .build();

            SearchResponse<BlogCategoryResponse> response = openSearchClient.search(searchRequest, BlogCategoryResponse.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source().getId()) // Chỉ lấy id
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error while fetching blog category IDs", e);
            return Collections.emptyList();
        }
    }


}

