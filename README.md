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



