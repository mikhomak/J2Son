

A simple class to convert *java object* to *Json format* using reflection   
It takes every **Getter** of the class and creates a fields with the value  
Supports **collections** and other **classes** as fileds  

## What is it for? And why not use google GSON?
Originally was made to convert **hybris** models to json to send it via REST  
GSON wouldn't work with hybris models because it converts the entire object(with context, history and other attribtues)  
Plus there was a need for custom filter and some field modification behind it  


## FILTER
To use the filter you can you create a map with the ClassName of the object as a key and a List of String of Attributes you want to convert
