package io.javabrains.inbox.Email;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends CassandraRepository<Email,UUID> {
    
}
