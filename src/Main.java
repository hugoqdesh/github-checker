import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.*;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner s = new Scanner(System.in);

            System.out.println("=== GITHUB PROFILE CHECKER ===\n");

            System.out.print("Enter github username: ");
            String username = s.nextLine().trim();

            String profile = fetchProfile(username);

            printUser(profile);

            System.out.print("\nDo you want to view recent activity? (y/n): ");
            String choice = s.nextLine().trim().toLowerCase();

            if(choice.equals("y") || choice.equals("yes")) {
                String response = fetchEvents(username);

                printActivity(response);
            } else {
                System.out.println("\nCya...");
            }
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private static String fetchProfile(String username) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/users/" + username))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println("Error fetching user profile: " + e.getMessage());
            return null;
        }
    }

    private static void printUser(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        String username = obj.has("login") && !obj.get("login").isJsonNull() ? obj.get("login").getAsString() : "N/A";
        String name = obj.has("name") && !obj.get("name").isJsonNull() ? obj.get("name").getAsString() : "N/A";
        String bio = obj.has("bio") && !obj.get("bio").isJsonNull() ? obj.get("bio").getAsString() : "N/A";
        String website = obj.has("blog") && !obj.get("blog").isJsonNull() ? obj.get("blog").getAsString() : "N/A";
        String location = obj.has("location") && !obj.get("location").isJsonNull() ? obj.get("location").getAsString() : "N/A";
        int publicRepos = obj.has("public_repos") ? obj.get("public_repos").getAsInt() : 0;
        int followers = obj.has("followers") ? obj.get("followers").getAsInt() : 0;
        int following = obj.has("following") ? obj.get("following").getAsInt() : 0;

        System.out.println("Username:       " + username);
        System.out.println("Name:           " + name);
        System.out.println("Bio:            " + bio);
        System.out.println("Website:        " + website);
        System.out.println("Location:       " + location);
        System.out.println("Public Repos:   " + publicRepos);
        System.out.println("Followers:      " + followers);
        System.out.println("Following:      " + following);
    }

    private static String fetchEvents(String username) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/users/" + username + "/events"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println("Error fetching data from GitHub: " + e.getMessage());
            return null;
        }
    }

    private static void printActivity(String json) {
        JsonArray events = JsonParser.parseString(json).getAsJsonArray();
        int count = 0;

        for (JsonElement eventElement : events) {
            if (count >= 6) break;

            JsonObject event = eventElement.getAsJsonObject();
            JsonObject payload = event.getAsJsonObject("payload");
            String type = event.get("type").getAsString();
            String repo = event.get("repo").getAsJsonObject().get("name").getAsString();

            switch (type) {
                case "CommitCommentEvent":
                    System.out.println("- Commented on a commit in " + repo);
                    break;
                case "CreateEvent":
                    String refType = payload.get("ref_type").getAsString();
                    System.out.println("- Created a new " + refType + " in " + repo);
                    break;
                case "DeleteEvent":
                    String refTypeDelete = payload.get("ref_type").getAsString();
                    System.out.println("- Deleted a " + refTypeDelete + " in " + repo);
                    break;
                case "ForkEvent":
                    System.out.println("- Forked " + repo);
                    break;
                case "GollumEvent":
                    System.out.println("- Updated the wiki in " + repo);
                    break;
                case "IssueCommentEvent":
                    String actionIssueComment = payload.get("action").getAsString();
                    System.out.println("- " + actionIssueComment + " a comment on an issue in " + repo);
                    break;
                case "IssuesEvent":
                    String action = payload.get("action").getAsString();
                    System.out.println("- " + action + " an issue in " + repo);
                    break;
                case "MemberEvent":
                    System.out.println("- Added a member to " + repo);
                    break;
                case "PublicEvent":
                    System.out.println("- Made " + repo + " public");
                    break;
                case "PullRequestEvent":
                    String actionPR = payload.get("action").getAsString();
                    System.out.println("- " + actionPR + " a pull request in " + repo);
                    break;
                case "PullRequestReviewEvent":
                    System.out.println("- Reviewed a pull request in " + repo);
                    break;
                case "PullRequestReviewCommentEvent":
                case "PullRequestReviewThreadEvent":
                    System.out.println("- Commented on a pull request review in " + repo);
                    break;
                case "PushEvent":
                    int commitCount = payload.getAsJsonArray("commits").size();
                    System.out.println("- Pushed " + commitCount + " commit" + (commitCount == 1 ? "" : "s") + " to " + repo);
                    break;
                case "ReleaseEvent":
                    System.out.println("- Released a new version in " + repo);
                    break;
                case "SponsorshipEvent":
                    System.out.println("- Sponsored a developer related to " + repo);
                    break;
                case "WatchEvent":
                    System.out.println("- Starred " + repo);
                    break;
                default:
                    break;
            }
            count++;
        }
    }
}
