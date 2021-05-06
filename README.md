# Assignment 1

## Running the Application

To run the application:

Right-click on the Application class in the org.magnum.dataup
package, Run As->Java Application

To stop the application:

Open the Eclipse Debug Perspective (Window->Open Perspective->Debug), right-click on
the application in the "Debug" view (if it isn't open, Window->Show View->Debug) and
select Terminate

## Overview

A popular use of cloud services is to manage media that is uploaded
from mobile devices. This assignment will create a very basic application
for uploading video to a cloud service and managing the video's metadata.
Once you are able to build this basic type of infrastructure, you will have
the core knowledge needed to create much more sophisticated cloud services.


## Instructions

First, clone this Git repository and import it into Eclipse or IntelliJ as a Gradle Project.
You can do this from the Eclipse "File" menu by selecting "Import". Expand the "Gradle"
option and then choose "Existing Gradle Project". Select the root folder of this 
project and then hit "Finish".

This assignment tests your ability to create a web application that
allows clients to upload videos to a server. The server allows clients
to first upload a video's metadata (e.g., duration, etc.) and then to
upload the actual binary data for the video. The server should support
uploading video binary data with a multipart request.

The test that is used to grade your implementation is AutoGradingTest
in the org.magnum.dataup package in src/test/java. **_You should use the
source code in the AutoGradingTest as the ground truth for what the expected
behavior of your solution is_.** Your app should pass this test without 
any errors. 

The HTTP API that you must implement so that this test will pass is as
follows:
 
GET /video
   - Returns the list of videos that have been added to the
     server as JSON. The list of videos does not have to be
     persisted across restarts of the server. The list of
     Video objects should be able to be unmarshalled by the
     client into a Collection<Video>.
   - The return content-type should be application/json

     
POST /video
   - The video metadata is provided as an application/json request
     body. The JSON should generate a valid instance of the 
     Video class when deserialized by Spring's default 
     Jackson library.
   - Returns the JSON representation of the Video object that
     was stored along with any updates to that object made by the server. 
   - **_The server should generate a unique identifier for the Video
     object and assign it to the Video by calling its setId(...)
     method._** 
   - No video should have ID = 0. All IDs should be > 0.
   - The returned Video JSON should include this server-generated
     identifier so that the client can refer to it when uploading the
     binary mpeg video content for the Video.
   - The server should also generate a "data url" for the
     Video. The "data url" is the url of the binary data for a
     Video (e.g., the raw mpeg data). The URL should be the _full_ URL
     for the video and not just the path (e.g., http://localhost:8080/video/1/data would
     be a valid data url). See the Hints section for some ideas on how to
     generate this URL.
     
POST /video/{id}/data
   - The binary mpeg data for the video should be provided in a multipart
     request as a part with the key "data". The id in the path should be
     replaced with the unique identifier generated by the server for the
     Video. A client MUST *create* a Video first by sending a POST to /video
     and getting the identifier for the newly created Video object before
     sending a POST to /video/{id}/data. 
   - The endpoint should return a VideoStatus object with state=VideoState.READY
     if the request succeeds and the appropriate HTTP error status otherwise.
     VideoState.PROCESSING is not used in this assignment but is present in VideoState.
   - Rather than a PUT request, a POST is used because, by default, Spring 
     does not support a PUT with multipart data due to design decisions in the
     Commons File Upload library: https://issues.apache.org/jira/browse/FILEUPLOAD-197
     
     
GET /video/{id}/data
   - Returns the binary mpeg data (if any) for the video with the given
     identifier. If no mpeg data has been uploaded for the specified video,
     then the server should return a 404 status code.
     
      
 The AutoGradingTest should be used as the ultimate ground truth for what should be 
 implemented in the assignment. If there are any details in the description above 
 that conflict with the AutoGradingTest, use the details in the AutoGradingTest 
 as the correct behavior and report the discrepancy on the course forums. Further, 
 you should look at the AutoGradingTest to ensure that
 you understand all of the requirements. 
 
 There is a VideoSvcApi interface that is annotated with Retrofit annotations in order
 to communicate with the video service that you will be creating. Your solution controller(s)
 should not directly implement this interface in a "Java sense" (e.g., you should not have
 YourSolution implements VideoSvcApi). Your solution should support the HTTP API that
 is described by this interface, in the text above, and in the AutoGradingTest. In some
 cases it may be possible to have the Controller and the client implement the interface,
 but it is not in this 
 
 Again -- the ultimate ground truth of how the assignment will be graded, is contained
 in AutoGradingTest, which shows the specific tests that will be run to grade your
 solution. You must implement everything that is required to make all of the tests in
 this class pass. If a test case is not mentioned in this README file, you are still
 responsible for it and will be graded on whether or not it passes. __Make sure and read
 the AutoGradingTest code and look at each test__!
 
 You should not modify any of the code in Video, VideoStatus,
 or AutoGradingTest. 

## Testing Your Implementation

To test your solution, run the AutoGradingTest. In addition, each time you push your code to GitHub,
a GitHub Action will run and execute this test. The results of running this test will be available
in GitHub. The primary measure for grading will be passing all of the tests.


## Submitting Your Assignment

To submit your assignment, you must push your final version to the main branch of your repository in 
GitHub prior to the assignment deadline. In addition, you should follow any other instructions in 
Brightspace.

 
## Provided Code

- __org.magnum.dataup.Video__: This is a simple class to represent the metadata for a video.
  You can create one using a builder like this:
  
```java
  Video video = Video.create().withContentType("video/mpeg")
			.withDuration(123).withSubject("Mobile Cloud")
			.withTitle("Programming Cloud Services for ...").build();
```
  You can also accept a Video from an application/json request body or return the
  JSON of a video like this:
```java
  	@RequestMapping(value = "/funny/video", method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v){
		// Do something with the Video
		// ...
		return v;
	}
```
- __org.magnum.dataup.VideoFileManager__: This is a class that you can (but are not required) to
  use to store video binary data to the file system. By default, it will store all videos to
  a "videos" directory in the current working directory. You can use this class as follows:
```java
    // Initialize this member variable somewhere with 
    // videoDataMgr = VideoFileManager.get()
    //
    private VideoFileManager videoDataMgr;

    // You would need some Controller method to call this...
  	public void saveSomeVideo(Video v, MultipartFile videoData) throws IOException {
  	     videoDataMgr.saveVideoData(video, videoData.getInputStream());
  	}
  	
  	public void serveSomeVideo(Video v, HttpServletResponse response) throws IOException {
  	     // Of course, you would need to send some headers, etc. to the
  	     // client too!
  	     //  ...
  	     videoDataMgr.copyVideoData(v, response.getOutputStream());
  	}
```
  
 
## Hints

- A valid solution is going to have at least one class annotated with @RestController or @Controller
- There will probably need to be at least 4 different methods annotated with @RequestMapping (or @GetMapping, @PostMapping, etc.) to
  implement the HTTP API described
- Any Controller method can take an HttpServletRequest or HttpServletResponse as parameters to 
  gain low-level access/control over the HTTP messages. Spring will automatically fill in these
  parameters when your Controller's method is invoked:
```java
        ...
        @RequestMapping("/some/path/{id}")
        public MyObject doSomething(
                   @PathVariable("id") String id, 
                   @RequestParam("something") String data,
                   HttpServletResponse response) {
         
            // Maybe you want to set the status code with the response
            // or write some binary data to an OutputStream obtained from
            // the HttpServletResponse object
            ....       
        }
        
```
- The IDs must be of type long. The tests send long values to the server and will generate
  400 response codes if you use an int.
- If you get an error 400, you have incorrectly specified the parameter values that the method
  should accept and their mapping to HTTP parameters.
- One of the Controller methods that is annotated with @RequestMapping is probably going to need 
  to take an HttpServletResponse object as a parameter and use this object to write out binary data 
  that should be sent to the client. 
- There are multiple ways to implement most pieces of the application.         
- One way to generate a unique ID for each video is to use an AtomicLong similar to this:
```java
        private static final AtomicLong currentId = new AtomicLong(0L);
	
	private Map<Long,Video> videos = new HashMap<Long, Video>();

  	public Video save(Video entity) {
		checkAndSetId(entity);
		videos.put(entity.getId(), entity);
		return entity;
	}

	private void checkAndSetId(Video entity) {
		if(entity.getId() == 0){
			entity.setId(currentId.incrementAndGet());
		}
	}
```



