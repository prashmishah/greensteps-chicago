**Green Steps Chicago**


**Last Revised**: February 17, 2026

**What is Green Steps Chicago?**

This document explains the carbon calculation feature in the Green Steps project. The purpose of this feature is to calculate carbon emissions based on user activities, such as travel. When a user logs an activity, the system calculates how much carbon dioxide (CO₂) was produced and stores that value in the database.

This helps users understand the environmental impact of their daily actions.

**Main Features**

- Users can log an activity (for example, travel distance).

- The system calculates carbon emissions automatically.

- Emission data is stored in the database.

- Users can view their past activities and emissions.

- The system connects frontend and backend through REST APIs.

- Data is stored using JPA with an H2 in-memory database.

**How It Works**

When a user enters activity information in the frontend, the data is sent to the backend using a POST request.

The backend controller receives the request and sends it to the service layer. The service layer performs the carbon calculation using a simple formula:

Carbon Emissions (kg CO₂) = Distance (km) × Emission Factor

After the calculation is completed, the result is stored in the Activities table. The backend then sends the result back to the frontend, where it is displayed to the user.

**Package Structure:**

**controller**

The controller handles HTTP requests from the frontend. It receives activity data and returns responses after processing.

**service**

The service layer contains the main business logic. It calculates carbon emissions and prepares the data before saving it.

**dto**

DTOs (Data Transfer Objects) are used to transfer data between the frontend and backend. They help organize request and response data without exposing internal database structure.

**entity**

Entities represent database tables.
The main entities are:

User

Activity

These are mapped using JPA annotations.

**Database Tables**

Users Table

id

username

password_hash

created_at

last_accessed_at

**Activities Table**

id

activity_type

distance_km

carbon_kg

start_time

end_time

user_id (foreign key)

**Assumptions and Limitations**

The emission factor used is fixed and simplified.

The system provides estimated values, not exact real-world emissions.

The database is in-memory, so data is lost when the backend stops.

Regional emission differences are not currently implemented.
