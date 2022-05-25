/*
 *
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@RestController
public class VideoController {

	// The name of the multipart form parameter that data is sent in
	public static final String DATA_PARAMETER = "videos";

	// The path variable for the ID of a video
	public static final String ID_PARAMETER = "id";

	// The path where we expect the VideoSvc to live
	public static final String VIDEO_SVC_PATH = "/video";

	// The path where we expect the video metadata lives
	public static final String INDIVIDUAL_VIDEO_PATH = VIDEO_SVC_PATH + "/{"+ID_PARAMETER+"}";

	// The path where we expect individual video data to live
	public static final String VIDEO_DATA_PATH = INDIVIDUAL_VIDEO_PATH + "/data";

	@Autowired
	private VideoFileManager videoFileManager;


	// This is a skeleton method for handling downloading of binary data. You will
	// need to fill in a small amount of the logic. This provides an example of how
	// to use Spring to marshall/unmarshall content types other than JSON.
	@GetMapping(VIDEO_DATA_PATH)
	public void getData(
			@PathVariable(ID_PARAMETER) long id,
			HttpServletResponse response) throws IOException {

		// @ToDo
		// Rewrite this line to find the video if it exists on the
		// server and assign it to the video variable.
		System.out.println("Inside get Data...");
		Video v = videoFileManager.getVideo(id);

		if(v != null){
			response.setContentType(v.getContentType());
			videoFileManager.copyVideoData(v, response.getOutputStream());
		}
		else {
			response.sendError(404, "Video not found");
		}
	}

	// This is a skeleton method for handling uploading of binary data. You will
	// need to fill in a small amount of the logic. This provides an example of how
	// to use Spring to marshall/unmarshall content types other than JSON.
	@PostMapping(VIDEO_DATA_PATH)
	public VideoStatus uploadVideo(
			@PathVariable(ID_PARAMETER) long id,
			//@RequestBody Video video,
			@RequestParam("data") MultipartFile videoData,
			HttpServletResponse response) throws IOException {

		System.out.println("Inside Get Video status...");
		// @ToDo
		// Rewrite this line to find the video if it exists on the
		// server and assign it to the video variable.
		Video video = videoFileManager.getVideo(id);
		VideoStatus status = null;

		if(video != null){
			// Sample to show you how to handle binary data in uploads
			videoFileManager.saveVideoData(video, videoData.getInputStream());
			status = new VideoStatus(VideoStatus.VideoState.READY);
		}
		else {
			response.sendError(404, "Video not found");
		}

		return status;
	}

	@GetMapping("/video")
	public Collection<Video> getVideoList()
	{
		System.out.println("Inside Get Video List ...");

		return  videoFileManager.getVideoCollection();
	}

	@GetMapping("/video/{id}")
	public Video getVideoWithID(@PathVariable(ID_PARAMETER) long id)
	{
		System.out.println("Inside Get Video with ID...");

		return videoFileManager.getVideo(id);
	}

	@PostMapping("/video")
	public Video addVideo(

			@RequestBody Video video,
			HttpServletResponse response) throws IOException {

		System.out.println("Inside Post...");
		// @ToDo
		// Rewrite this line to find the video if it exists on the
		// server and assign it to the video variable.

		VideoStatus status = null;

		if (video != null) {
			video.setId(generateId());//(UUID.randomUUID().getMostSignificantBits());
			videoFileManager.addVideoToCollection(video);
			// Sample to show you how to handle binary data in uploads
			//videoFileManager.saveVideoData(video, videoData.getInputStream());
			status = new VideoStatus(VideoStatus.VideoState.READY);

			System.out.println(video.getId());
		} else {
			response.sendError(404, "Video not found");
		}

		return video;
	}
		public long generateId()
		{

			long id= -1;
			while (id<1)
			{
				id = UUID.randomUUID().getMostSignificantBits();
			}

			return id;
		}

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 *
	 *
	 ________  ________  ________  ________          ___       ___  ___  ________  ___  __
	 |\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \
	 \ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_
	 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \
	 \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \
	 \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
	 \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|

	 *
	 */

}
