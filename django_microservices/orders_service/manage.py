#!/usr/bin/env python
"""Django's command-line utility for administrative tasks."""
import os
import sys

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

def main():
    """Run administrative tasks."""
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'orders_service.settings')
    try:
        from django.core.management import execute_from_command_line
        from django.conf import settings
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc

    if len(sys.argv) > 1:
        sys.argv += ["runserver",f"{settings.PORT}:{settings.PORT}"]

    execute_from_command_line(sys.argv)

if __name__ == '__main__':
    main()
