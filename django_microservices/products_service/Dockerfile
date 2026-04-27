# Building layer
FROM python:3.11-slim
# set environment variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV DJANGO_SETTINGS_MODULE=products_services.settings

WORKDIR /app
# install dependencies
RUN apt-get update && apt-get install -y \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*
# Copy requirements.txt from parent directory
COPY ../requirements.txt .
# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt \
    gunicorn psycopg2-binary

# Copy application
COPY . .
# create non-root user
RUN useradd -m -s /bin/bash django && chown -R django:django /app
USER django

# expose port here or in K3s
EXPOSE 8090
# HealthCheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8090/actuator/health || exit 1

CMD ["gunicorn", "--bind", "0.0.0.0:8090", "products_services.wsgi:application"]