
package com.innodealing.bpms.domain;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.innodealing.wsdl.domain package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SynDept_QNAME = new QName("http://service.admin.sso.pop.czt.cn/", "synDept");
    private final static QName _SynUserResponse_QNAME = new QName("http://service.admin.sso.pop.czt.cn/", "synUserResponse");
    private final static QName _SynUser_QNAME = new QName("http://service.admin.sso.pop.czt.cn/", "synUser");
    private final static QName _SynDeptResponse_QNAME = new QName("http://service.admin.sso.pop.czt.cn/", "synDeptResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.innodealing.wsdl.domain
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SynDept }
     * 
     */
    public SynDept createSynDept() {
        return new SynDept();
    }

    /**
     * Create an instance of {@link SynUserResponse }
     * 
     */
    public SynUserResponse createSynUserResponse() {
        return new SynUserResponse();
    }

    /**
     * Create an instance of {@link SynUser }
     * 
     */
    public SynUser createSynUser() {
        return new SynUser();
    }

    /**
     * Create an instance of {@link SynDeptResponse }
     * 
     */
    public SynDeptResponse createSynDeptResponse() {
        return new SynDeptResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SynDept }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.admin.sso.pop.czt.cn/", name = "synDept")
    public JAXBElement<SynDept> createSynDept(SynDept value) {
        return new JAXBElement<SynDept>(_SynDept_QNAME, SynDept.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SynUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.admin.sso.pop.czt.cn/", name = "synUserResponse")
    public JAXBElement<SynUserResponse> createSynUserResponse(SynUserResponse value) {
        return new JAXBElement<SynUserResponse>(_SynUserResponse_QNAME, SynUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SynUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.admin.sso.pop.czt.cn/", name = "synUser")
    public JAXBElement<SynUser> createSynUser(SynUser value) {
        return new JAXBElement<SynUser>(_SynUser_QNAME, SynUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SynDeptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.admin.sso.pop.czt.cn/", name = "synDeptResponse")
    public JAXBElement<SynDeptResponse> createSynDeptResponse(SynDeptResponse value) {
        return new JAXBElement<SynDeptResponse>(_SynDeptResponse_QNAME, SynDeptResponse.class, null, value);
    }

}
