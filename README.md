# Flask CI/CD Lab Project

## 📁 Project Structure

### Core Application Files
- **`app.py`** - Flask application
- **`Dockerfile`** - Docker configuration for the Flask app
- **`docker-compose.yml`** - Docker Compose configuration (Flask + Nginx)
- **`nginx.conf`** - Nginx proxy configuration

### Pipeline Files (Learning Progression)

#### 1. **`01-beginner-pipeline.groovy`** - Level 1: Basic Pipeline
- **Purpose**: Learn basic Jenkins pipeline syntax
- **What it does**: Simple stages with echo commands
- **Use when**: First time learning Jenkins pipelines
- **Jenkins setup**: Copy this script into a new Pipeline job

#### 2. **`02-production-pipeline.groovy`** - Level 2: Production Pipeline
- **Purpose**: Learn how to build from actual project files
- **What it does**: 
  - Validates project structure
  - Builds Docker image from your files
  - Tests and deploys the application
- **Use when**: You want to build from real project files
- **Jenkins setup**: Copy this script into a new Pipeline job

#### 3. **`Jenkinsfile`** - Level 3: Git-Based Pipeline
- **Purpose**: Standard way to define pipelines in Git repositories
- **What it does**: Same as production pipeline but uses Git integration
- **Use when**: Working with Git repositories
- **Jenkins setup**: Create Pipeline job with "Pipeline script from SCM" and point to this directory

## 🚀 Learning Path

1. **Start with**: `01-beginner-pipeline.groovy` - Learn basic concepts
2. **Then try**: `02-production-pipeline.groovy` - Learn real-world approach
3. **Finally**: `Jenkinsfile` - Learn Git integration

## 📋 How to Use

### For Beginner Pipeline:
1. Go to Jenkins → New Item → Pipeline
2. Copy content from `01-beginner-pipeline.groovy`
3. Paste in Pipeline script section
4. Save and run

### For Production Pipeline:
1. Go to Jenkins → New Item → Pipeline
2. Copy content from `02-production-pipeline.groovy`
3. Paste in Pipeline script section
4. Save and run

### For Git-Based Pipeline:
1. Go to Jenkins → New Item → Pipeline
2. Select "Pipeline script from SCM"
3. Choose Git
4. Repository URL: `/home/sahal/cicd-demo/lab-flask`
5. Script Path: `Jenkinsfile`
6. Save and run

## ✅ What You'll Learn

- Basic Jenkins pipeline syntax
- How to build Docker images in pipelines
- How to test and deploy applications
- How to work with Git repositories
- Real-world CI/CD practices
