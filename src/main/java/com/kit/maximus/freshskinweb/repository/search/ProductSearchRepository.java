package com.kit.maximus.freshskinweb.repository.search;

import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.InlineGet;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
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

}
