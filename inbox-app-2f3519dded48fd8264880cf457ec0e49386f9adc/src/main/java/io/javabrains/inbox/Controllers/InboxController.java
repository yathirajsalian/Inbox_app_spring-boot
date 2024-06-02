package io.javabrains.inbox.Controllers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.javabrains.inbox.EmailList.EmailListItem;
import io.javabrains.inbox.EmailList.EmailListItemRepository;
import io.javabrains.inbox.Folder.Folder;
import io.javabrains.inbox.Folder.FolderRepository;
import io.javabrains.inbox.Folder.FolderService;
import io.javabrains.inbox.Folder.UnreadEmailStats;
import io.javabrains.inbox.Folder.UnreadEmailStatsRepository;



@Controller
public class InboxController {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

   

    @GetMapping(value = "/")
    public String homePage(
    @RequestParam(required = false) String folder,
    @AuthenticationPrincipal OAuth2User principal,
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
       
       model.addAttribute("stats", folderService.mapCountToLabels(userId));
       model.addAttribute("userName",principal.getAttribute("name"));


       //Fetch Messages
       if(!StringUtils.hasText(folder))
       {
            folder = "Inbox";
       }
      
       List<EmailListItem> emailList = emailListItemRepository.findAllByKey_IdAndKey_Label(userId, folder);

       model.addAttribute("emailList", emailList);
       PrettyTime p = new PrettyTime();
        emailList.stream().forEach(emailItem -> {
           UUID timeUuid =  emailItem.getKey().getTimeUuid();
           Date emailDateTime = new Date(Uuids.unixTimestamp(timeUuid));
           emailItem.setAgoTimeString(p.format(emailDateTime));

        });

       model.addAttribute("emailList", emailList);
       model.addAttribute("folderName",folder);
       return "inbox-page";

       
    }
    
}
/*
 * {
  "clientId": "PdyXyrPhPPUPLfyTDkHGShfK",
  "secret": "vtlEQ7nucWrUhCPMoaqjU-WZZdg9gi,oG-R5FtmH3Tv7z4sQFDJO+R0phUjLeOgPDYjtKRmdPAMz+4rOYZcUo1Y-a2iT-DRytcj33+_pTyWeYBAo9HyT_W5Iz_9Fx,_I",
  "token": "AstraCS:PdyXyrPhPPUPLfyTDkHGShfK:8fb6bb648100b95d597dcb13a671de90f2ac214d7a42bd1d798342e5ef0a3e1e"
}
 */