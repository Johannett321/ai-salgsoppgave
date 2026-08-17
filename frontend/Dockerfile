# Use the official Node.js image to build our React app
FROM node:20-alpine AS build

# Set the working directory
WORKDIR /usr/src/app

# Copy the package.json and package-lock.json
COPY ./package*.json ./

# Install the app dependencies
RUN npm ci

# Copy the rest of the application files
COPY . .

# Create React App baker miljøvariablene inn i bygget, så de må settes her.
ARG REACT_APP_BACKEND_URL=http://localhost:8080
ARG REACT_APP_FRONTEND_URL=http://localhost:3000
ENV REACT_APP_BACKEND_URL=$REACT_APP_BACKEND_URL
ENV REACT_APP_FRONTEND_URL=$REACT_APP_FRONTEND_URL

# Build the React app
RUN npm run build

# Serve the React app using a lightweight static server
FROM node:20-alpine

RUN npm install -g serve

COPY --from=build /usr/src/app/build /app/build

EXPOSE 3000

CMD ["serve", "-s", "/app/build", "-l", "3000"]
