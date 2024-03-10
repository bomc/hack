<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring Boot with Plain HTML</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
</head>
<body>
    <h1>Hello from Spring Boot!</h1>
    <p>This is a plain HTML page served by a Spring Boot application.</p>
    <label for="correlationId">Enter Correlation ID:</label>
    <input type="text" id="correlationId" name="correlationId">
    <button id="myButton">Click Me</button>
    
    <table id="resultTable">
        <tr>
            <th>Round Cycle</th>
            <th>Result</th>
        </tr>
    </table>

    <script>
        $(document).ready(function(){
            $("#myButton").click(function(){
                var correlationId = $("#correlationId").val();
                $.ajax({
                    url: "/api/buttonClick",
                    type: "POST",
                    data: JSON.stringify({ correlationId: correlationId }),
                    contentType: "application/json",
                    success: function(response) {
                        displayResult(response, false);
                    },
                    error: function(xhr, status, error) {
                        displayResult("Error occurred: " + error, true);
                    }
                });
            });
        });

        function displayResult(result, isError) {
            var table = document.getElementById("resultTable");
            var row = table.insertRow(-1);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);

            // Create SVG representation of the round cycle
            var svg = '<svg width="50" height="50"><circle cx="25" cy="25" r="20" fill="' + (isError ? 'red' : 'green') + '" /></svg>';
            
            cell1.innerHTML = svg;
            cell2.innerHTML = result;

            // Set text color based on isError flag
            cell2.style.color = isError ? 'red' : 'green';
        }
    </script>
</body>
</html>







import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.*;

@Configuration
public class WatchServiceConfiguration {

    @Bean
    public WatchService watchService() throws IOException {
        // Define the directory to be watched
        Path directoryPath = Paths.get("path/to/your/directory");

        // Create WatchService object
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // Register the directory with the WatchService for all events
        directoryPath.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

        // Start watching the directory
        watchDirectory(directoryPath, watchService);

        return watchService;
    }

    private void watchDirectory(Path directoryPath, WatchService watchService) {
        new Thread(() -> {
            try {
                // Infinite loop for continuously watching the directory
                while (true) {
                    // Retrieve and process events from the WatchService
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        // Delegate the event handling to fileChangeHandler bean
                        applicationContext.getBean(FileChangeHandler.class).handleFileEvent(event);
                    }
                    // Reset the key
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
And here's a simple example of a FileChangeHandler bean:

java
Copy code
import org.springframework.stereotype.Component;
import java.nio.file.WatchEvent;

@Component
public class FileChangeHandler {

    public void handleFileEvent(WatchEvent<?> event) {
        // Handle file event here
        System.out.println("File event: " + event.context() + ", kind: " + event.kind());
    }
}
For the unit test, let's ensure that all types of file events are properly handled:

java
Copy code
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.nio.file.*;

import static org.mockito.Mockito.*;

@SpringBootTest
public class WatchServiceDemoApplicationTests {

    @Autowired
    private WatchService watchService;

    @Mock
    private FileChangeHandler fileChangeHandler;

    @Test
    public void testWatchService() throws IOException, InterruptedException {
        // Given
        Path directoryPath = Paths.get("path/to/your/directory");
        WatchKey watchKey = mock(WatchKey.class);

        // Mock file events
        when(watchService.take()).thenReturn(watchKey);
        when(watchKey.pollEvents()).thenReturn(createFileEvents());

        // When
        watchDirectory(directoryPath, watchService);

        // Then
        verify(fileChangeHandler, times(3)).handleFileEvent(any(WatchEvent.class));
    }

    private Iterable<WatchEvent<?>> createFileEvents() {
        // Mock file events
        WatchEvent<Path> createEvent = mock(WatchEvent.class);
        WatchEvent<Path> modifyEvent = mock(WatchEvent.class);
        WatchEvent<Path> deleteEvent = mock(WatchEvent.class);

        when(createEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(modifyEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_MODIFY);
        when(deleteEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_DELETE);

        return List.of(createEvent, modifyEvent, deleteEvent);
    }

    private void watchDirectory(Path directoryPath, WatchService watchService) {
        new Thread(() -> {
            try {
                // Infinite loop for continuously watching the directory
                while (true) {
                    // Retrieve and process events from the WatchService
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        // Delegate the event handling to fileChangeHandler bean
                        fileChangeHandler.handleFileEvent(event);
                    }
                    // Reset the key
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
In this test:

We mock the WatchService and define mock file events to simulate various file event types (create, modify, delete).
We verify that the fileChangeHandler bean's handleFileEvent method is called three times, corresponding to the three simulated file events.
The watchDirectory method is simplified since we're not using the Spring context within the test.
Ensure to replace "path/to/your/directory" with the actual path to the directory you want to observe.
