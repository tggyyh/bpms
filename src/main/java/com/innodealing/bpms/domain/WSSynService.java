
package com.innodealing.bpms.domain;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "WSSynService", targetNamespace = "http://service.admin.sso.pop.czt.cn/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WSSynService {


    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "synDept", targetNamespace = "http://service.admin.sso.pop.czt.cn/", className = "com.innodealing.bpms.domain.SynDept")
    @ResponseWrapper(localName = "synDeptResponse", targetNamespace = "http://service.admin.sso.pop.czt.cn/", className = "com.innodealing.bpms.domain.SynDeptResponse")
    public String synDept();

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "synUser", targetNamespace = "http://service.admin.sso.pop.czt.cn/", className = "com.innodealing.bpms.domain.SynUser")
    @ResponseWrapper(localName = "synUserResponse", targetNamespace = "http://service.admin.sso.pop.czt.cn/", className = "com.innodealing.bpms.domain.SynUserResponse")
    public String synUser();

}
