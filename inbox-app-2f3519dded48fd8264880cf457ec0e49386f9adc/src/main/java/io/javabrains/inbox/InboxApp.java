package io.javabrains.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.javabrains.inbox.Email.Email;
import io.javabrains.inbox.Email.EmailRepository;
import io.javabrains.inbox.Email.EmailService;
import io.javabrains.inbox.EmailList.EmailListItem;
import io.javabrains.inbox.EmailList.EmailListItemKey;
import io.javabrains.inbox.EmailList.EmailListItemRepository;
import io.javabrains.inbox.Folder.Folder;
import io.javabrains.inbox.Folder.FolderRepository;
import io.javabrains.inbox.Folder.UnreadEmailStatsRepository;

@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired FolderRepository folderRepository;
	
	@Autowired EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

	@PostConstruct
	public void init()
	{
		folderRepository.save(new Folder("yathirajsalian","Family","blue"));
		folderRepository.save(new Folder("yathirajsalian","Home","green"));
		folderRepository.save(new Folder("yathirajsalian","Work","yellow"));

		
		for(int i=0;i<10;i++)
		{			
			emailService.sendEmail("yathirajsalian", Arrays.asList("yathirajsalian","abc"), "Hello World", "Email number"+i);

		}

		emailService.sendEmail("abc", Arrays.asList("def","abc"), "Hello World", "Email number ");
	}

}
