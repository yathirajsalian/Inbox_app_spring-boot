package io.javabrains.inbox.Email;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.javabrains.inbox.EmailList.EmailListItem;
import io.javabrains.inbox.EmailList.EmailListItemKey;
import io.javabrains.inbox.EmailList.EmailListItemRepository;
import io.javabrains.inbox.Folder.UnreadEmailStatsRepository;

@Service
public class EmailService {


    @Autowired private EmailRepository emailRepositroy;
    @Autowired private EmailListItemRepository emailListItemRepository;
    @Autowired private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from,List<String> to,String subject,String body){

        Email email = new Email();
        email.setTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setBody(body);
        email.setId(Uuids.timeBased());

        emailRepositroy.save(email);

        to.forEach(toId -> {
            EmailListItem item = createEmailListItem(to, subject, email, toId,"Inbox");
            emailListItemRepository.save(item);
            unreadEmailStatsRepository.incrementUnreadCounter(toId, "Inbox");
        });

        EmailListItem sentItemsEntry = createEmailListItem(to, subject, email, from,"Sent Items");
        sentItemsEntry.setUnread(false);
        emailListItemRepository.save(sentItemsEntry);

    }

    private EmailListItem createEmailListItem(List<String> to, String subject, Email email, String itemOwner,String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setId(itemOwner);
        key.setLabel(folder);
        key.setTimeUuid(email.getId());
        EmailListItem item = new EmailListItem();
        item.setKey(key);
        item.setTo(to);
        item.setSubject(subject);
        item.setUnread(true);
        return item;
    }

    public boolean doesHaveAccess(Email email,String userId)
    {
        return (userId.equals(email.getFrom()) || email.getTo().contains(userId));
    }

    public String getReplySubject(String subject)
    {
        return "Re: "+subject;
    }

    public String getReplyBody(Email email)
    {
        return "\n \n \n ------------------------------ \n" + 
        "From: "+email.getFrom()+"\n"+
        "To: "+email.getTo()+"\n \n"+ email.getBody();
    }
}
