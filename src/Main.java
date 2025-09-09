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

            System.out.println("=== GITHUB ACTIVITY CHECKER ===\n");

            System.out.print("Enter github username: ");
            String username = s.nextLine().trim();

            System.out.println("\n" + username + "'s recent activity:\n");

            String responseBody = fetchEvents(username);

            if (responseBody == null) {
                System.out.println("Failed to fetch data from GitHub");
                return;
            }

            printActivity(responseBody);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
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
