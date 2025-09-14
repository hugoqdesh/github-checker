<h3 align="center">GitHub Checker</h3>
<p align="center">A simple Java console application that retrieves and displays GitHub user information and recent activity using GitHub’s REST API</p>

## About The Project

This Java application allows you to:

- Fetch and display a GitHub user's profile details (username, name, bio, location, etc.)
- View their recent GitHub activity (last 6 public events)
- Interact via a terminal-based UI

It uses Java’s built-in HTTP Client and Google's Gson for parsing JSON responses from the GitHub API.

## Example

```
=== GITHUB PROFILE CHECKER ===

Enter github username: github
Username:       github
Name:           GitHub
Bio:            How people build software.
Website:        https://github.com/about
Location:       San Francisco, CA
Public Repos:   523
Followers:      59295
Following:      0

Do you want to view recent activity? (y/n): y
- Starred github/spec-kit
- Starred github/spec-kit
- Deleted a branch in github/docs
- created a comment on an issue in github/docs
- closed a pull request in github/docs
- opened a pull request in github/docs
```
