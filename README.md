# Department Activity Monitor
This application is meant to run on a raspberry pi with an attached 
camera and installed in a department within a store. It uses computer
vision to detect customers as they move and produce an event to a kafka
topic when a customer enters or leaves the department. Consumers of this 
kafka topic can create analytical reports with the data such as which 
departments get the most traffic, customers' shopping patterns, etc.