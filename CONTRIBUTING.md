# Contributing to AutoTotem Light

Thank you for your interest in contributing to AutoTotem Light! This document provides guidelines for contributing to the project.

## How to Contribute

### Reporting Issues
- Use the GitHub issue tracker to report bugs or request features
- Provide detailed information about the issue
- Include Minecraft version, Fabric Loader version, and any relevant logs

### Submitting Changes
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Test your changes thoroughly
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods focused and small

### Testing
- Test your changes with the target Minecraft version
- Ensure compatibility with Lunar Client
- Test all configuration options
- Verify no regressions in existing functionality

## Development Setup

### Prerequisites
- Java 21 or later
- Git
- Gradle (or use the included wrapper)

### Building
```bash
./gradlew build
```

### Running in Development
```bash
./gradlew runClient
```

## License
By contributing, you agree that your contributions will be licensed under the MIT License.
