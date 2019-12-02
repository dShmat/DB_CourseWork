package com.ibagroup.collectme;

import com.ibagroup.collectme.config.ApplicationProperties;
import com.ibagroup.collectme.service.*;
import com.ibagroup.collectme.service.dto.PeriodDTO;
import com.ibagroup.collectme.service.dto.UserDTO;
import com.ibagroup.collectme.service.util.LdapContextSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component
public class ScheduledTasks {
    private static final int ATTRIBUTE_SIZE = 3;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserManagerService userManagerService;

    @Autowired
    private ManagerProjectService managerProjectService;

    @Autowired
    private PeriodService periodService;

    private final ApplicationProperties applicationProperties;

    public ScheduledTasks(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    //every day at 02:00
    @Scheduled(cron = "0 0 2 * * ?")
    @EventListener(ApplicationReadyEvent.class)
    public void loadUsersFromLDAP() {

        log.debug("(Scheduled) Trying to update db with new users from Ldap... ");
        LdapContextSource ldapContextSource = new LdapContextSourceUtil(applicationProperties).getContextSource();
        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
        List<UserDTO> userDTOS;
        userDTOS = ldapTemplate.search(query().base("")
                .attributes("sn", "mail", "givenname")
                .where("objectclass").is("person"),
            (AttributesMapper<UserDTO>) attrs -> {
                //ATTRIBUTE_SIZE = 3;
                if (attrs.size() == ATTRIBUTE_SIZE) {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setLogin((String) attrs.get("mail").get());
                    userDTO.setEmail((String) attrs.get("mail").get());
                    userDTO.setFirstName((String) attrs.get("givenname").get());
                    userDTO.setLastName((String) attrs.get("sn").get());
                    return userDTO;
                }
                return null;
            });

        while (userDTOS.remove(null)) ;
        userDTOS.forEach(userDTO -> {
            if (!userDTO.getLogin().isEmpty() && !userDTO.getEmail().isEmpty() &&
             !userService.findOneByEmail(userDTO.getEmail()).isPresent()) {
                userService.handleUsers(userDTO);
            }
        });
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @EventListener(ApplicationReadyEvent.class)
    public void generateForNextMonth(){
        periodService.findCurrent().ifPresent(currentPeriod -> {
            if(!periodService.findNext().isPresent()){
                PeriodDTO periodDTO = periodService.generateForNextMonth();
                userManagerService.generateForNextMonth(periodDTO);
                managerProjectService.generateForNextMonth(periodDTO);
                reportService.generateForNextMonth(periodDTO);
            }
        });
    }
}
