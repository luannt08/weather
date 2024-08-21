### Design

The design of the application is a top priority. My goal is to focus on what users truly need from a weather application while also considering my own requirements. I spent considerable time conceptualizing the design and ultimately drew inspiration from the iOS weather application, which serves as a solid reference point.
I conducted studies on color schemes for various weather conditions, UV index levels, and air quality indicators to make the application more useful. One reason for creating this app is that I couldn’t directly utilize some of this information from the iOS platform.
The application will have two main screens:
1. Home Screen: Displays all added cities with brief weather information. The background color is based on each city's current weather condition.
2. Details Screen: Provides a more detailed view, including a complete weather forecast with an hourly forecast for the next 24 hours and a daily forecast for the next 7 days.
 
   <img width="361" alt="Screenshot 2024-08-20 at 20 55 04" src="https://github.com/user-attachments/assets/8566d192-d3f2-45f4-9838-0876665f8cc1"><img width="361" alt="Screenshot 2024-08-20 at 20 55 17" src="https://github.com/user-attachments/assets/b2a80e7a-e0c3-43b2-845d-b0163b3fcfcd">
   <img width="361" alt="Screenshot 2024-08-20 at 20 55 30" src="https://github.com/user-attachments/assets/6a1abfb0-509a-4aa6-9efe-e48bd9426661"><img width="360" alt="Screenshot 2024-08-20 at 20 55 44" src="https://github.com/user-attachments/assets/a4e6cbd4-5588-45e7-8451-27e2eff0ee1b">



### Networking

Initially, I planned two simple networking flows using WorkManager. However, handling errors effectively made the first flow a bit more complex:
1. When the application is opened, a periodic worker is enqueued to fetch data for all added cities every 15 minutes. The worker is initially delayed by 5 minutes, while the HomeViewModel triggers an immediate request for the cities’ weather data. My goal is to ensure users receive the latest data and are notified of any errors when they open the application. While WorkManager is useful, there’s an issue where the HomeViewModel might miss the error event because WorkManager executes too quickly in some cases (e.g., no internet connection), causing the error to be emitted before the HomeViewModel starts collecting the error flow (SharedFlow). Additionally, I want the worker to run only when the network is connected, preventing it from executing if the user opens the app without internet access.
                               <img width="605" alt="Screenshot 2024-08-20 at 22 10 52" src="https://github.com/user-attachments/assets/924f6c4a-27e9-47a4-b0ff-5bdc11e8e480">

                                    
2. When a user searches for a city, the weather forecast for that city is fetched, allowing the user to decide whether to add it.
I opted against using pull-to-refresh for data updates, relying instead on the worker, because the weather data from the server isn’t updated in real time. Providing users with an option to refresh frequently would unnecessarily strain the server.
There is a critical bug in the API where the latitude and longitude returned in the response differ slightly from those used in the request (just a small difference after the decimal point). If your database relies on precise latitude and longitude, this can lead to issues like having multiple cities with the same name. I encountered this bug, which resulted in significant rework.

In this application, there are two types of errors: internet connection errors and unexpected errors. For connection errors, users can usually resolve the issue by turning on Wi-Fi or enabling mobile data (3G/4G). The other category involves errors caused by the server or the application itself, which users cannot address. That’s why I don’t provide detailed information to the user and instead just print the stack trace. Following best practices, we should implement a logging system that allows developers to track these errors during both development and production.

### Architecture

I follow Google’s recommended architecture, where the repository serves as the single source of truth and handles all data logic. Although I initially considered implementing clean architecture, it seemed excessive for the scope of this application. In terms of best practices, choosing the right approach is key.

In the repository, there are three types of models:
* _Realm models for Realm database entities.
* _Response models for server responses.
* _Data models for the data layer, used to communicate with the ViewModel.
Additionally, models with the UI prefix represent the presentation layer.


Unit testing is a valuable practice for ensuring the quality and functionality of an application. It helps identify and fix bugs early in the development process. Unfortunately, due to time constraints for this assignment, I was unable to write more on the topic of unit testing.

##### * To use this source please config apiKey and url in localproperties file.
##### * API source: https://www.weatherbit.io/api
