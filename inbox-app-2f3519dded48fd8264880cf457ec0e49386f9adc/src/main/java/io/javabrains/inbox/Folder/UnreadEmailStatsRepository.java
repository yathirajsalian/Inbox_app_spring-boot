package io.javabrains.inbox.Folder;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UnreadEmailStatsRepository extends CassandraRepository<UnreadEmailStats,String>{

    List<UnreadEmailStats> findAllById(String id);



    @Query("update unread_email_stats set unreadcount = unreadcount + 1 where user_id = ?0 and label = ?1")
    public void incrementUnreadCounter(String id, String label);
    @Query("update unread_email_stats set unreadcount = unreadcount - 1 where user_id = ?0 and label = ?1")
    public void decrementUnreadCounter(String userId,String label);
    
}
