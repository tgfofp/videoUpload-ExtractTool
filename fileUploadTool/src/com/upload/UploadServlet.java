package com.upload;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;

import com.extractor.DecodeAndCaptureFrames;

@WebServlet("/uploaded")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 50) // 50MB
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = -2455870336252572807L;

	/**
	 * handles file upload
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uploadDir = System.getProperty("user.home") + File.separator + "uploads";

		// creates the save directory if it does not exists
		File fileSaveDir = new File(uploadDir);
		File fileSaveDir1 = new File(uploadDir + File.separator + "output");

		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		} else {
			FileUtils.cleanDirectory(fileSaveDir);
		}

		if (!fileSaveDir1.exists()) {
			fileSaveDir1.mkdir();
		} else {
			FileUtils.cleanDirectory(fileSaveDir1);
		}

		String filePath = null;
		// gets all the parts of the request and writes it
		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			filePath = uploadDir + File.separator + fileName;
			part.write(filePath);
		}

		// object which creates the screenshots from a video file
		DecodeAndCaptureFrames dacp = new DecodeAndCaptureFrames(filePath, String.valueOf(fileSaveDir1));

		// setting the attributes to the Servlet request
		request.setAttribute("message", "Upload has been done successfully!");
		request.setAttribute("filePath", uploadDir);

		// Forwards the request data of the request to the .jsp file
		getServletContext().getRequestDispatcher("/result.jsp").forward(request, response);
	}

	// Extracts file name from HTTP header content-disposition
	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}
}