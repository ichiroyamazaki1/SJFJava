package application;

import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SJF extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try (Scanner input = new Scanner(System.in)) {
            do {
                System.out.println("-------------------------");
                System.out.println("   SHORTEST JOB FIRST    ");
                System.out.println("-------------------------");
                System.out.println(" ");

                System.out.print("Enter the number of processes: ");
                int n = input.nextInt();

                int[] process = new int[n];
                int[] arrivalTime = new int[n];
                int[] burstTime = new int[n];
                int[] completedTime = new int[n];
                int[] turnaroundTime = new int[n];
                int[] waitingTime = new int[n];
                int totalWaitingTime = 0;
                int totalTurnaroundTime = 0;
                float avgWaitingTime, avgTurnaroundTime;
                int totalTime = 0;

                for (int i = 0; i < n; i++) {
                    process[i] = i + 1;
                    System.out.print("Enter Arrival Time for Process " + process[i] + ": ");
                    arrivalTime[i] = input.nextInt();
                    System.out.print("Enter Burst Time for Process " + process[i] + ": ");
                    burstTime[i] = input.nextInt();
                    totalTime += burstTime[i];
                }
                System.out.println("P\t|\tAT\t|\tBT");
                for (int i = 0; i < n; i++) {
                    System.out.println(process[i] + "\t" + "|" + "\t" + arrivalTime[i] + "\t" + "|" + "\t" + burstTime[i]);
                }
                int currentTime = 0;
                int completedProcesses = 0;
                while (completedProcesses < n) {
                    int shortestProcess = findShortestProcess(arrivalTime, burstTime, n, currentTime);
                    if (shortestProcess == -1) {
                        currentTime++;
                    } else {
                        currentTime = Math.max(currentTime, arrivalTime[shortestProcess]);
                        completedTime[shortestProcess] = currentTime + burstTime[shortestProcess];
                        turnaroundTime[shortestProcess] = completedTime[shortestProcess] - arrivalTime[shortestProcess];
                        waitingTime[shortestProcess] = turnaroundTime[shortestProcess] - burstTime[shortestProcess];

                        totalWaitingTime += waitingTime[shortestProcess];
                        totalTurnaroundTime += turnaroundTime[shortestProcess];

                        currentTime += burstTime[shortestProcess];
                        burstTime[shortestProcess] = 0;
                        completedProcesses++;
                    }
                }
                avgWaitingTime = (float) totalWaitingTime / n;
                avgTurnaroundTime = (float) totalTurnaroundTime / n;

                System.out.println("\nProcess Table");
                launchGanttChartWindow(primaryStage, completedTime, totalTime, avgWaitingTime, avgTurnaroundTime);

                System.out.println("\nP\t|\tWT\t|\tTAT\t|\tCT");
                for (int i = 0; i < n; i++) {
                    System.out.println(process[i] + "\t" + "|" + "\t" + waitingTime[i] + "ms" + "\t" + "|" + "\t" +
                            turnaroundTime[i] + "ms" + "\t" + "|" + "\t" + completedTime[i] + "ms");
                }
                System.out.printf("\nAverage Waiting Time: %.2f" + "ms \n", avgWaitingTime);
                System.out.printf("Average Turnaround Time: %.2f" + "ms", avgTurnaroundTime);
                System.out.println(" ");

                System.out.print("Do you want to run the program again? (Y/N): ");
                String runAgain = input.next();

                if (!runAgain.equalsIgnoreCase("Y")) {
                    System.out.println("");
                    System.out.println("Program Closed. Thank you!");
                    System.out.println("Submitted by: Maria Zharima Marie Barrientos from BSCPE-2A");
                    break;
                }
            } while (true);
        }
    }
    private static int findShortestProcess(int[] arrivalTime, int[] burstTime, int n, int currentTime) {
        int shortestProcess = -1;
        int shortestBurst = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            if (arrivalTime[i] <= currentTime && burstTime[i] < shortestBurst && burstTime[i] > 0) {
                shortestBurst = burstTime[i];
                shortestProcess = i;
            }
        }
        return shortestProcess;
    }
    private void launchGanttChartWindow(Stage primaryStage, int[] completionTimes, int totalTime,
    			double averageWaitingTime, double averageTurnaroundTime) {
    		Pane root = new Pane();
    		double scale = 50.0;

    		Text title = new Text("Gantt Chart Table");
    		title.setFont(new Font("Arial", 20));
    		title.setX(0);
    		title.setY(30);

    		Text totalTimeText = new Text("Total Time: " + totalTime);
    		totalTimeText.setFont(new Font("Arial", 14));
    		totalTimeText.setX(0);
    		totalTimeText.setY(160);

    		Text avgWaitingTimeText = new Text("Average Waiting Time: " + String.format("%.2fms", averageWaitingTime));
    		avgWaitingTimeText.setFont(new Font("Arial", 14));
    		avgWaitingTimeText.setX(0);
    		avgWaitingTimeText.setY(180);

    		Text avgTurnaroundTimeText = new Text(
    				"Average Turnaround Time: " + String.format("%.2fms", averageTurnaroundTime));
    		avgTurnaroundTimeText.setFont(new Font("Arial", 14));
    		avgTurnaroundTimeText.setX(0);
    		avgTurnaroundTimeText.setY(200);

    		root.getChildren().addAll(title, totalTimeText, avgWaitingTimeText, avgTurnaroundTimeText);

    		double xPos = 0;
    		for (int i = 0; i < completionTimes.length; i++) {
    			double width = (i == 0 ? completionTimes[i] : completionTimes[i] - completionTimes[i - 1]) * scale;

    			Rectangle rect = new Rectangle(xPos, 50, width, 60);
    			rect.setStroke(Color.BLACK);
    			rect.setFill(Color.PINK);

    			Text text = new Text(xPos + 5, 75, "Process " + (i + 1));

    			root.getChildren().addAll(rect, text);
    			xPos += width;
    		}

    		for (int i = 0; i <= totalTime; i++) {
    			Text timeText = new Text(scale  * i - (i < 10 ? 3 : 7), 125, String.valueOf(i));
    			root.getChildren().add(timeText);
    		}

    		ScrollPane scrollPane = new ScrollPane(root);
    		scrollPane.setPrefViewportWidth(600);
    		scrollPane.setPrefViewportHeight(300);
    		scrollPane.setPannable(true);

    		primaryStage.setTitle("Shortest Job First");
    		primaryStage.setScene(new Scene(scrollPane));
    		primaryStage.show();
    	}
    }
