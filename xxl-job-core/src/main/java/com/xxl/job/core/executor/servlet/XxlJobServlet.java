//package com.xxl.job.client.netcom.servlet;
//
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.xxl.job.client.handler.HandlerRouter;
//
//
///**
// * remote job client on http
// * @author xuxueli 2015-12-19 18:36:47
// */
//@Deprecated
//public class XxlJobServlet extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//
//    /**
//     * Default constructor. 
//     */
//    public XxlJobServlet() {
//        // TODO Auto-generated constructor stub
//    }
//    
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		request.setCharacterEncoding("UTF-8");
//		response.setCharacterEncoding("UTF-8");
//		
//		Map<String, String> _param = new HashMap<String, String>();
//		if (request.getParameterMap()!=null && request.getParameterMap().size()>0) {
//			for (Object paramKey : request.getParameterMap().keySet()) {
//				if (paramKey!=null) {
//					String paramKeyStr = paramKey.toString();
//					_param.put(paramKeyStr, request.getParameter(paramKeyStr));
//				}
//			}
//		}
//		
//		String resp = HandlerRouter.action(_param);
//		response.getWriter().append(resp);
//		return;
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		doGet(request, response);
//	}
//
//}
