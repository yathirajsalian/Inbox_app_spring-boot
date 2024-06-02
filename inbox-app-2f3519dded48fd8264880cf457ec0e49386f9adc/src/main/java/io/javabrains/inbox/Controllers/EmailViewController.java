package io.javabrains.inbox.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import io.javabrains.inbox.Email.Email;
import io.javabrains.inbox.Email.EmailRepository;
import io.javabrains.inbox.Email.EmailService;
import io.javabrains.inbox.EmailList.EmailListItem;
import io.javabrains.inbox.EmailList.EmailListItemKey;
import io.javabrains.inbox.EmailList.EmailListItemRepository;
import io.javabrains.inbox.Folder.Folder;
import io.javabrains.inbox.Folder.FolderRepository;
import io.javabrains.inbox.Folder.FolderService;
import io.javabrains.inbox.Folder.UnreadEmailStatsRepository;


@Controller
public class EmailViewController {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping(value = "/emails/{id}")
    public String emailView(@RequestParam String folder,
    @AuthenticationPrincipal OAuth2User principal,
    @PathVariable UUID id,
    Model model)
    {
        

        if(principal == null || !StringUtils.hasText(principal.getAttribute("login")))
        {
            return "index";
       }

       //Fetch Folders
       String userId = principal.getAttribute("login");
       List<Folder> userFolders= folderRepository.findAllById(userId);
       model.addAttribute("userFolders", userFolders);
       
       List<Folder> defaultFolders= folderService.fetchDefaultFolders(userId);
       model.addAttribute("defaultFolders", defaultFolders);

       model.addAttribute("userName",principal.getAttribute("name"));

       

       Optional<Email> optionalEmail = emailRepository.findById(id);
       if(!optionalEmail.isPresent())
       {
            return "inbox-page";
       }
       
       Email email = optionalEmail.get();
       String toIds = String.join(",",email.getTo());

       //check if user is allowed to see the email
       if(!emailService.doesHaveAccess(email, userId))
       {
            return "redirect:/";
       }
       

       model.addAttribute("email", email);
       model.addAttribute("toIds",toIds);

       EmailListItemKey key = new EmailListItemKey();
       key.setId(userId);
       key.setLabel(folder);
       key.setTimeUuid(email.getId());

       Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);
       if(optionalEmailListItem.isPresent())
       {
            EmailListItem emailListItem = optionalEmailListItem.get();
            if(emailListItem.isUnread())
            {
                emailListItem.setUnread(false);
                emailListItemRepository.save(emailListItem);
                unreadEmailStatsRepository.decrementUnreadCounter(userId, folder);
            }
       }

       model.addAttribute("stats", folderService.mapCountToLabels(userId));
       return "email-page";
    }
    
}
