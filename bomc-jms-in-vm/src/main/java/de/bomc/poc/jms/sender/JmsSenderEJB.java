package de.bomc.poc.jms.sender;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

/**
 * A ejb that send a jms message.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 15.03.2018
 */
@LocalBean
@Stateless
public class JmsSenderEJB {

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory jmsConnectionFactory;
    @Resource(mappedName = "java:/queue/test")
    private Queue testQueue;
    @Inject
    private JMSContext jmsContext;
    
    public void sendJmsMessage() {

    }
    
}
