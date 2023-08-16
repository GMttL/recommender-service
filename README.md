# recommender-service

## Abstract

This service provides personalised recommendations for flatmate matching based on a user's profile and preferences.
It uses unsupervised learning, namely clustering, to group users together based on their similarities.

## Objective

To improve the user experience of our flatmate matching platform by providing personalised recommendations.

## Research

The R notebook `BCM_DATA_V2.Rmd` contains the research that we conducted to determine the best clustering method, number of clusters, and number of PCA components for our recommender service.

The notebook is divided into the following sections:

- `Cleaning`
- `Exploratory Data Analysis`
- `Feature Engineering`
  - Feature Selection
  - Feature Encoding
  - Feature Scaling
  - TF-IDF
  - Outlier Detection
  - Interaction Features
- `Model Selection & PCA`
  - Centroid Based Clustering
  - Hierarchical Clustering
  - Density Based Clustering
  - Fuzzy Clustering
  - Affinity Propagation Clustering
  - Spectral Clustering
- `Conclusion`

## Data

To overcome the challenges of data privacy, we opted using synthetic data in our models.
The data was generated using `Mostly.ai` which is a synthetic data generation platform that uses GANs to generate statistically and structurally identical data to the original.

The generated data has the following features:

- `age`: Numeric
- `gender`: Categorical (name, female, other)
- `language`: Categorical
- `nationality`: Categorical
- `occupation`: Categorical (student, professional, other)
- `smoker`: Categorical (yes,no)
- `any pets`: Categorical (yes,no)
- `interests`: Text Data (e.g. music, hiking, cooking, etc.)
- `budget`: Numeric
- `room wanted`: Categorical (single, double)
- `areas`: Text data (e.g. Zone 1, Elephant & Castle, Highbury, etc.)
- `min term`: Numeric, unit is months
- `max term`: Numeric, unit is months
- `amenities`: Text Data (e.g. roof terrace, gym, furnished, etc.)

Flatmate Preferences: 
- `smokers OK?`: Categorical (yes,no)
- `pets OK?`: Categorical (yes,no)
- `occupation Req?`: Categorical (student, professional, no preference)
- `min age`: Numeric
- `max age`: Numeric
- `gender Req`: Categorical (female, male, other)

## Design

The services uses a 4-PCA Component PAM with Cosine Similarity as a distance metric to cluster users.

PAM is a popular clustering algorithm known for its efficiency and accuracy. It can, however, be computationally expensive.
Due to this, the service uses CLARANS which is an extension to PAM to perform clustering on large data sets.

#### Data: 
The service manages three documents in MongoDB: TrainedModel, PreProcessingMeta, and ClusteredProfile.

- TrainedModel: Stores the trained model and its PCA components.
- PreProcessingMeta: Stores the meta data used to preprocess the data. (This is required so we don't have to preprocess the whole dataset again)
- ClusteredProfile: Stores the clustered profiles formed by: (1) Original User Data (2) Processed Profile (ready to be ingested by our model) (3) The Cluster they belong to

## Endpoints:

- /api/recommendation/matches/{uid} - Returns a list of recommended users for the given user from the most to least similar. (There is no rate-limiting for now, the service returns the entire cluster with one call)

## Project Structure:

The service was developed using a modular approach to provide easy navigation and separation of concerns. The project is structured as follows:

- `config`: Configuration files
- `controller`: Web Controllers to manage endpoints
- `converter`: Classes that help MongoDB serialise and deserialise more complex objects
- `entity`: Persistent data model
- `exception`: Custom exceptions
- `listener`: Event listeners. Checks if a model is trained at startup
- `logic`: Model and Clustering LOGIC
- `repository`: Data access layer
- `service`: Business logic layer
- `util`: Miscellaneous classes


## Limitations

The service is not currently operational due to its dependency on another service called `onboarding-service` which is used to create user profiles and expose them through an API.
The `onboarding-service` is tied to my personal mongoDB and making it public at this time would not be prudent.

## How to use:

- Use the `BCM_DATA_V2.Rmd` notebook and `syntethic_data.csv` to inspect the data and run the models.
- Use the `tests` to see if the service works as expected locally.


Do let me know if there is anything else I can help with.