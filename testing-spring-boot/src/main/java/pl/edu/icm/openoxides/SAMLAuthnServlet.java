package pl.edu.icm.openoxides;

import javax.servlet.http.HttpServlet;

public class SAMLAuthnServlet extends HttpServlet {
//    public static final String SERVLET_PATH = "/authn/performSAMLAuthn";
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException
//    {
//        Set<SessionTrackingMode> sessionTracking = req.getServletContext().getEffectiveSessionTrackingModes();
//        if (sessionTracking.contains(SessionTrackingMode.URL) && sessionTracking.size() == 1)
//        {
//            error(resp, "SAML authentication can not be used with the URL " +
//                    "method of session tracking at the moment.");
//            return;
//        }
//        SAMLAuthenticationContext input = getInput(req, resp);
//        if (input == null)
//            return;
//        AuthnRequestDocument reqDoc;
//        try
//        {
//            reqDoc = AuthnRequestDocument.Factory.parse(input.getSamlRequest());
//        } catch (XmlException e)
//        {
//            error(resp, "Received unparseable SAML authentication request " +
//                    "to be forwarded to the IdP: " + e.toString() + input.getSamlRequest());
//            return;
//        }
//
//        configureHttpResponse(resp);
//        String form = HttpPostBindingSupport.getHtmlPOSTFormContents(SAMLMessageType.SAMLRequest,
//                input.getIdpUrl(),
//                reqDoc.xmlText(), null);
//        PrintWriter writer = resp.getWriter();
//        writer.write(form);
//        writer.flush();
//    }
//
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException
//    {
//        String samlResponse = req.getParameter("SAMLResponse");
//        if (samlResponse == null)
//        {
//            error(resp, "No SAML authentication response received. This servlet should be only used " +
//                    "from the login pipeline of the UNICORE portal only.");
//            return;
//        }
//        SAMLAuthenticationContext input = getInput(req, resp);
//        if (input == null)
//            return;
//        input.setSamlResponse(samlResponse);
//
//        String returnUrl = input.getReturnUrl();
//        resp.sendRedirect(returnUrl);
//    }
//
//    private SAMLAuthenticationContext getInput(HttpServletRequest req, HttpServletResponse resp) throws IOException
//    {
//        SAMLAuthenticationContext input = (SAMLAuthenticationContext) req.getSession().getAttribute(
//                SAMLAuthenticationContext.SESSION_KEY);
//        if (input == null)
//        {
//            error(resp, "No authn input available in session. This servlet should be only used " +
//                    "from the login pipeline of the UNICORE portal only.");
//            return null;
//        }
//        return input;
//    }
//
//    private void error(HttpServletResponse resp, String msg) throws IOException
//    {
//        PrintWriter w = resp.getWriter();
//        String ret = errorForm.replace("__ERROR_MSG", msg);
//        w.write(ret);
//        w.flush();
//    }
//
//    private void configureHttpResponse(HttpServletResponse resp)
//    {
//        resp.setContentType("text/html; charset=utf-8");
//        resp.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
//        resp.setHeader("Pragma","no-cache");
//        resp.setDateHeader("Expires", -1);
//    }
//
//    private static final String errorForm =
//            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" " +
//                    "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">" +
//                    "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">" +
//                    "<body>" +
//                    "<p>" +
//                    "<strong>ERROR:</strong> __ERROR_MSG" +
//                    "</p>" +
//                    "</body></html>";
}
