package com.kit.maximus.freshskinweb.repository.search;

import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.InlineGet;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.termvectors.Term;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductSearchRepository {

    OpenSearchClient openSearchClient;

    public void indexProduct(ProductResponseDTO product) {
        try {
            IndexRequest<ProductResponseDTO> request = new IndexRequest.Builder<ProductResponseDTO>()
                    .index("products") // Tên index
                    .id(String.valueOf(product.getId())) // ID sản phẩm
                    .document(product) // Dữ liệu sản phẩm
                    .build();

            openSearchClient.index(request);
            System.out.println("Indexed product: " + product.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProductResponseDTO getProductById(Long productId) {
        try {
            // Tạo GetRequest với ID sản phẩm
            GetRequest getRequest = new GetRequest.Builder()
                    .index("products")
                    .id(String.valueOf(productId))
                    .build();

            GetResponse<ProductResponseDTO> getResponse = openSearchClient.get(getRequest, ProductResponseDTO.class);

            if (getResponse.found()) {
                return getResponse.source();
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error while fetching product by ID", e);
            return null;
        }
    }

    public boolean updateProduct(ProductResponseDTO productResponseDTO) {
        try {
            // Tạo UpdateRequest để cập nhật sản phẩm với kiểu dữ liệu rõ ràng
            UpdateRequest<ProductResponseDTO, ProductResponseDTO> updateRequest = new UpdateRequest.Builder<ProductResponseDTO, ProductResponseDTO>()
                    .index("products")  // Chỉ định index
                    .id(String.valueOf(productResponseDTO.getId()))  // Chỉ định ID của sản phẩm
                    .doc(productResponseDTO) // Thông tin cập nhật (dữ liệu mới)
                    .build();

            // Thực hiện yêu cầu cập nhật
            UpdateResponse<ProductResponseDTO> updateResponse = openSearchClient.update(updateRequest, ProductResponseDTO.class);

            InlineGet<ProductResponseDTO> get = updateResponse.get();
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

    public boolean deleteProduct(Long productId) {
        try {
            // Tạo DeleteRequest để xóa sản phẩm dựa trên ID
            DeleteRequest deleteRequest = new DeleteRequest.Builder()
                    .index("products")  // Chỉ định index
                    .id(String.valueOf(productId))  // Chỉ định ID của sản phẩm cần xóa
                    .build();

            // Thực hiện yêu cầu xóa
            DeleteResponse deleteResponse = openSearchClient.delete(deleteRequest);

            // Kiểm tra xem xóa có thành công không
            if ("deleted".equals(deleteResponse.result().toString())) {
                log.info("Product with ID {} was deleted. Result: {}", productId, deleteResponse.result());
                return true;
            } else {
                log.warn("Failed to delete product with ID {}. Result: {}", productId, deleteResponse.result());
                return false;
            }
        } catch (IOException e) {
            // Log lỗi khi gặp sự cố trong quá trình xóa
            log.error("Error while deleting product with ID {}", productId, e);
            return false;
        }
    }




    public List<ProductResponseDTO> searchByTitle(String title, int size) {
        try {
            // Tạo truy vấn match với fuzziness, prefix_length và operator
            MatchQuery matchQuery = new MatchQuery.Builder()
                    .field("title") // Chỉ định trường "title"
                    .query(FieldValue.of(title)) // Dữ liệu truy vấn là "title"
                    .fuzziness("auto") // Fuzziness tự động
                    .operator(Operator.And) // Cấu hình để tất cả các từ đều phải xuất hiện
                    .build();

            // Xây dựng truy vấn với MatchQuery
            Query searchQuery = new Query.Builder()
                    .match(matchQuery)
                    .build();

            // Tạo SearchRequest, chỉ lấy số lượng kết quả tối đa là "size"
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("products") // Chỉ định index
                    .query(searchQuery) // Thêm truy vấn vào request
                    .size(size) // Số lượng kết quả trả về
                    .build();

            // Gửi yêu cầu tìm kiếm
            SearchResponse<ProductResponseDTO> response = openSearchClient.search(searchRequest, ProductResponseDTO.class);

            // Trích xuất danh sách các đối tượng ProductResponseDTO từ kết quả
            return response.hits().hits().stream()
                    .map(Hit::source) // Lấy source (ProductResponseDTO)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Lỗi khi tìm kiếm sản phẩm trên OpenSearch", e);
            return Collections.emptyList();
        }
    }

    public List<ProductResponseDTO> findBySlugs(String slug, String status, boolean deleted) {
        try {
            List<Query> queries = new ArrayList<>();

            // Tìm kiếm theo slug (của category)
            TermQuery termQuerySlug = new TermQuery.Builder()
                    .field("category.slug.keyword")  // Đúng tên trường là "category.slug.keyword"
                    .value(FieldValue.of(slug))
                    .build();

            // Tìm kiếm theo slug của category cha
            TermQuery termQueryCateParent = new TermQuery.Builder()
                    .field("category.parent.slug.keyword")
                    .value(FieldValue.of(slug))
                    .build();

            // Tìm kiếm theo slug của category ông nội
            TermQuery termQueryGrandParent = new TermQuery.Builder()
                    .field("category.parent.parent.slug.keyword")
                    .value(FieldValue.of(slug))
                    .build();

            // Thêm điều kiện tìm theo status
            TermQuery termQueryStatus = new TermQuery.Builder()
                    .field("status.keyword")
                    .value(FieldValue.of(status))
                    .build();

            // Thêm điều kiện tìm theo deleted
            TermQuery termQueryDeleted = new TermQuery.Builder()
                    .field("deleted")
                    .value(FieldValue.of(deleted))
                    .build();

            // Thêm các query vào danh sách
            queries.add(new Query.Builder().term(termQuerySlug).build());
            queries.add(new Query.Builder().term(termQueryCateParent).build());
            queries.add(new Query.Builder().term(termQueryGrandParent).build());
            queries.add(new Query.Builder().term(termQueryStatus).build());
            queries.add(new Query.Builder().term(termQueryDeleted).build());

            // Sử dụng BoolQuery để kết hợp các điều kiện (OR)
            BoolQuery boolQuery = new BoolQuery.Builder()
                    .should(queries)  // Các điều kiện OR
                    .minimumShouldMatch("1")  // Ít nhất một điều kiện phải khớp
                    .build();

            Query searchQuery = new Query.Builder().bool(boolQuery).build();


            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("products")
                    .query(searchQuery)
                    .size(500)
                    .build();

            // Thực hiện tìm kiếm
            SearchResponse<ProductResponseDTO> response = openSearchClient.search(searchRequest, ProductResponseDTO.class);

            // Kiểm tra và trả về kết quả
            if (!response.hits().hits().isEmpty()) {
                // Chuyển đổi kết quả từ hits thành danh sách ProductResponseDTO
                return response.hits().hits().stream()
                        .map(Hit::source)  // Chuyển đổi hit thành đối tượng ProductResponseDTO
                        .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }

        } catch (Exception ex) {
            log.error("Error while fetching products by slug", ex);
            return Collections.emptyList();
        }
    }




    public ProductResponseDTO findBySlug(String slug, String status, boolean deleted) {
        try {
            // Điều kiện tìm theo slug (exact match)
            TermQuery termQuerySlug = new TermQuery.Builder()
                    .field("slug.keyword")
                    .value(FieldValue.of(slug))
                    .build();

            // Điều kiện tìm theo deleted (true/false)
            TermQuery termQueryDeleted = new TermQuery.Builder()
                    .field("deleted")
                    .value(FieldValue.of(deleted))
                    .build();

            // Tạo danh sách query
            List<Query> queries = new ArrayList<>();
            queries.add(new Query.Builder().term(termQuerySlug).build());
            queries.add(new Query.Builder().term(termQueryDeleted).build());

            MatchQuery matchQueryStatus = new MatchQuery.Builder()
                    .field("status")
                    .query(FieldValue.of(status))
                    .build();
            queries.add(new Query.Builder().match(matchQueryStatus).build());


            // Kết hợp tất cả các điều kiện bằng BoolQuery (AND)
            BoolQuery boolQuery = new BoolQuery.Builder()
                    .must(queries)
                    .build();

            Query searchQuery = new Query.Builder()
                    .bool(boolQuery)
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("products")
                    .query(searchQuery)
                    .size(1)
                    .build();

            SearchResponse<ProductResponseDTO> response = openSearchClient.search(searchRequest, ProductResponseDTO.class);

            // Kiểm tra kết quả
            if (!response.hits().hits().isEmpty()) {
                return response.hits().hits().getFirst().source();
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error while fetching product by slug", e);
            return null;
        }
    }


    public List<ProductResponseDTO> getProductByCategoryIDs(List<Long> categoryIds, String status, boolean deleted, int size) {
        try {
            Query query = new Query.Builder()
                    .bool(b -> b
                            .must(m -> m.terms(t -> t
                                    .field("category.id")
                                    .terms(ts -> ts.value(categoryIds.stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())))
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

            // Tạo request với sort theo position giảm dần
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("products")
                    .query(query)
                    .size(size)
                    .sort(s -> s
                            .field(f -> f
                                    .field("position")
                                    .order(SortOrder.Desc)
                            )
                    )
                    .build();

            SearchResponse<ProductResponseDTO> response = openSearchClient.search(searchRequest, ProductResponseDTO.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error while fetching products by categoryIds", e);
            return Collections.emptyList();
        }
    }



}
