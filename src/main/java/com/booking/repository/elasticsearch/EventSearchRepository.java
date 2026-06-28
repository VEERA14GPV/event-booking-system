package com.booking.repository.elasticsearch;

import com.booking.document.EventDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface EventSearchRepository
        extends ElasticsearchRepository<EventDocument, String> {

    /*
     * Full-text search across name, description,
     * category, and venue.
     *
     * name and category are boosted since exact/near
     * matches there are the most relevant to users.
     */
    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["name^3", "description", "category^2", "venue^2"],
                "fuzziness": "AUTO"
              }
            }
            """)
    Page<EventDocument> searchByKeyword(
            String keyword,
            Pageable pageable);
}
