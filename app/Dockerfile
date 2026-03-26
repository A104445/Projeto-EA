FROM node:22-alpine

WORKDIR /app
COPY package.json package-lock.json ./
COPY . .
RUN npm ci
EXPOSE 5174
CMD ["npx", "quasar", "dev", "--hostname", "0.0.0.0", "--port", "5174"]
