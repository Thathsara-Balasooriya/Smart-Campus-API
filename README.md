# Smart-Campus-API

## **Smart Campus: Sensor \& Room Management API**





#### ***Project Overview***



**This project is a high-performance RESTful API designed for the University's "Smart Campus" infrastructure. Developed with Java JAX-RS (Jersey) and Maven, it efficiently manages a large-scale network of Rooms and Sensors (e.g., Temperature, CO2). The system implements advanced REST patterns, including Sub-Resource Locators for log retrieval and Exception Mapping for resilient error management.**



#### ***Build \& Launch Instructions***



**To build and run this API locally, follow these steps:**



1. **Clone the Repository: git clone <your-repo-link>**
2. **Navigate to Directory: cd <project-folder>**
3. **Build with Maven: mvn clean install**
4. **Run the Server: mvn exec:java (or use your specific Main class command).**
* &#x09;**The API will be accessible at: http://localhost:8080/api/v1**



#### ***Postman Testing***



***STEP 01 :***

* ***A JSON response containing metadata like the version ("v1") and links to your primary collections (rooms and sensors). This demonstrates HATEOAS for your report.***
* GET - http://localhost:8080/api/v1



***STEP 02 :***

* ***Create a Room***
* POST - http://localhost:8080/api/v1/rooms
* Body - {"id": "ENG-402", "name": "Engineering Lab", "capacity": 35}



* GET - http://localhost:8080/api/v1/rooms/ENG-402
* ***The full JSON object of the room you just created.***



***STEP 03 :***

* ***Add a Sensor***
* POST - http://localhost:8080/api/v1/sensors
* Body - {"id": "CO2-99", "type": "CO2", "status": "ACTIVE", "roomId": "ENG-402"}



* ***Add a sensor with a invalid roomId.***
* POST - http://localhost:8080/api/v1/sensors
* Body - {"id": "CO2-99", "type": "CO2", "status": "ACTIVE", "roomId": "NULL-ZONE"}



* GET - http://localhost:8080/api/v1/sensors?type=CO2
* ***Show that only sensors matching that type are returned.***



***STEP 04 :***

* ***Update the currentValue.***
* POST - http://localhost:8080/api/v1/sensors/CO2-99/read
* Body - {"value": 450.0}
* GET - http://localhost:8080/api/v1/sensors/CO2-99



* ***Update the sensor status to "MAINTENANCE".***
* PUT - http://localhost:8080/api/v1/sensors/CO2-99
* Headers - Content-Type to application/json
* Body - {"id": "CO2-99", "type": "CO2", "status": "MAINTENANCE", "currentValue": 450.0, "roomId": "ENG-402"}



* ***Add a reading to the "MAINTENANCE" sensor.***
* POST - http://localhost:8080/api/v1/sensors/CO2-99/read
* Body - {"value": 465.5}



***STEP 05 :***

* ***Return the sensor status to "ACTIVE".***
* PUT - http://localhost:8080/api/v1/sensors/CO2-99
* Headers - Content-Type to application/json
* Body - {"id": "CO2-99", "type": "CO2", "status": "ACTIVE", "currentValue": 465.5, "roomId": "ENG-402"}



* ***Delete the room with the ACTIVE sensor.***
* DELETE - http://localhost:8080/api/v1/rooms/ENG-402



* ***Delete the sensor first and then delete the room.***
* DELETE - http://localhost:8080/api/v1/sensors/CO2-99
* DELETE - http://localhost:8080/api/v1/rooms/ENG-402


Conceptual Report

Part 1
Q-01

By default, in JAX-RS, resource objects are created per request. This implies that, for each request received, a fresh object from the resource class will be created.

Such an approach guarantees that:

* Statelessness of resource classes
* No shared mutable state is present in resource objects
* Issues related to thread safety are kept to a minimum in resource objects

Nevertheless, in the application that I have implemented, the use of in-memory structures (like Maps/Lists in the Service layer or storage layer) is used to maintain Rooms, Sensors, and Readings.

As these structures are shared among various requests, the decision about the life cycle has several consequences:

* The resources being created per request imply that the data should not be persisted in resource objects
* The shared data should be persisted in some centralized entities (like Service layer objects or even static storage)
Such shared structures need to be handled carefully to prevent:
Race condition
Data inconsistency in case of concurrent requests

Thus, I made sure that:

* Data is maintained in central services
* Logical access patterns are maintained (which can be further synchronized when scaled)

Q-02
HATEOAS (Hypermedia As The Engine Of Application State) is a key constraint of REST, where API responses include links to related resources.
Instead of clients relying on static documentation, responses dynamically guide them through the API.
Example from my design:
* Discovery endpoint returns: 
/rooms 
/sensors 
Benefits:
1. Self-discoverable API 
Clients don't need hardcoded URLs 
2. Loose coupling 
Backend structure can evolve without breaking clients 
3. Improved developer experience 
Easier navigation and integration 
4. Future scalability 
Supports dynamic workflows 
Compared to static documentation, HATEOAS:
* Reduces dependency on external docs 
* Makes APIs more adaptive and robust 

Part 2

Q-1

When returning a list of rooms:
Option 1: Only IDs
* Pros: Smaller payload (better for bandwidth) 
* Cons: Requires additional requests to fetch details
Option 2: Full Room Objects (Used in my API)
* Pros: Reduces number of client requests 
* Pros: Provides complete information in one call 
* Cons: Slightly higher payload size
Justification of My Design:
I choose to return full room objects since:
* Increases customer efficiency
* Minimizes back-and-forth movement
* Ideal for datasets of medium sizes (uses an in-memory database)
It's all about balancing efficiency and completeness, but mine favors efficiency.

Q-2
Yes- the DELETE operation in my API is idempotent.
Explanation:
If a client sends:
DELETE /rooms/{id}
* First request: 
Room is deleted ? returns success (e.g., 204 or 200) 
* Second request (same ID): 
Room no longer exists ? returns 404 
Why it is still idempotent:
Idempotency means:
Repeating the same request results in the same system state
After the first deletion:
* The room is already removed 
* Further DELETE requests do not change anything 

Part-3
Q-1

In my API, I used:
@Consumes(MediaType.APPLICATION_JSON)
This tells JAX-RS to only accept JSON input.
If client sends:
* text/plain 
* application/xml 
Then JAX-RS will:
* Reject the request 
* Return HTTP 415 Unsupported Media Type 
Why this is important:
* Ensures strict API contract 
* Prevents invalid data parsing 
* Maintains consistency 

Q-2

I used:
GET /sensors?type=CO2
instead of:
/sensors/type/CO2
Why QueryParam is better:
1. Designed for filtering 
Query params naturally represent optional filters 
2. Flexible 
Multiple filters can be added:
/sensors?type=CO2&status=ACTIVE
3. Cleaner API design 
Path represents resource hierarchy 
Query represents filtering/searching 
4. Optional usage 
/sensors still works without filters 
Path-based filtering is:
* Less flexible 
* Harder to extend


Part-4

Q-1
I implemented sub-resource pattern:
/sensors/{id}/readings
Benefits:
1. Separation of concerns 
SensorResource ? handles sensors 
SensorReadingResource ? handles readings 
2. Improved maintainability 
Smaller, focused classes 
3. Scalability 
Easy to extend nested resources 
4. Cleaner architecture 
Avoids god class with too many responsibilities 
Without this pattern:
* One large controller becomes hard to manage


Part - 5
Q-1

When creating a sensor with a non-existent room:
* Request JSON is valid 
* But referenced resource is invalid  
Why 422 is better:
* 422 = semantic error in request content 
* 404 = resource itself not found 
Here:
* Endpoint exists 
* Payload is wrong 
So 422 is more accurate

Q-2
Exposing Java stack traces is dangerous because attackers can see:
* Internal class names 
* Package structure 
* Framework details 
* Line numbers 
* Potential vulnerabilities 
Risks:
* Reverse engineering the system 
* Identifying weak points 
* Crafting targeted attacks 
Solution:
* Global Exception Mapper 
* Returns generic:
500 Internal Server Error
* No internal details leaked

Q-3
I implemented logging using:
* ContainerRequestFilter 
* ContainerResponseFilter 
Advantages:
1. Centralized logging 
No need to add logs in every method 
2. Cleaner code 
Resource classes remain focused 
3. Reusable 
Works across entire API 
4. Cross-cutting concern handling 
Logging, security, auth can be handled similarly 
If logging was inside each method:
* Code duplication 
* Hard to maintain 
* Easy to miss cases
