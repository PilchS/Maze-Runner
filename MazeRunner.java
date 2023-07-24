import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MazeRunner {

    public static void main(String[] args){ 
        try{ 
        var maze = LoadMaze("maze_txt.txt");
        var path = MakePaths(maze, false);
        printSolution(maze, path);
        //printMaze(maze);
        }catch(Exception e){
            System.out.println("Exception: "+ e);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public MazeRunner(String filename) {
        try {
            var maze = LoadMaze(filename);
            // printMaze(maze);
            var path = MakePaths(maze, false);
            printSolution(maze, path);
        } catch (Exception e) {
            System.out.println("EXCEPTION encountered: " + e);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private static ArrayList<ArrayList<Integer>> LoadMaze(String path) throws Exception {

        ArrayList<ArrayList<Integer>> maze = new ArrayList<ArrayList<Integer>>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = null;
        while ((line = reader.readLine()) != null) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for (char a : line.toCharArray()) {
                if (a == 'S') {
                    temp.add(1);
                } else if (a == 'F') {
                    temp.add(2);
                } else if (a == 'C') {
                    temp.add(0);
                } else if (a == 'W') {
                    temp.add(-1);
                } else if (a != '\t') {
                    throw new Exception("Unrecognized charcter");
                }
            }
            maze.add(temp);
        }
        reader.close();
        return maze;
    }

    private static void NumberAdjacent(ArrayList<ArrayList<Integer>> maze, ArrayList<ArrayList<Integer>> paths, int x_init,
            int y_init, int iter, boolean verbose) {
        if (verbose) System.out.println(String.format("\n >> moving to [%d, %d]:", y_init, x_init));
        
        int[][] x_y = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (var mod : x_y){
            int x = x_init + mod[0];
            int y = y_init + mod[1];
            if (verbose) System.out.print(String.format("[%d]\t", iter));
            if (verbose) System.out.print(String.format(" >> Checking [%d, %d]", y, x));
            try {
                int value = maze.get(x).get(y);
                if (value == 0) {
                    // System.out.print(String.format("[%d]\t", iter));
                    if (verbose) System.out.print(" >> (path)");
                    if (paths.get(x).get(y) == 0) {
                        if (verbose) System.out.print(" >> (empty)");
                        paths.get(x).set(y, iter);
                        if (verbose) System.out.print(String.format(" >> taking [%d, %d] at depth ", y, x, iter));
                        // System.out.println(paths.get(x));
                        NumberAdjacent(maze, paths, x, y, iter + 1, verbose);
                    } else {
                        if (verbose) System.out.print(String.format(" >> (taken) [depth %d]", paths.get(x).get(y)));
                    }
                } else {
                    if (value == 1) {
                        if (verbose) System.out.print(String.format(" >> (START) [depth %d]", paths.get(x).get(y)));
                    } else if (value == 2) {
                        if (verbose) System.out.print(String.format(" >> (FINISH) [depth %d]", paths.get(x).get(y)));
                        paths.get(x).set(y, iter);
                    } else {
                        if (verbose) System.out.print(String.format(" >> (wall)", x, y));
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                if (verbose) System.out.print(String.format(" >> (bounds)", x, y));
            }
            if (verbose) System.out.println();
        }
        if (verbose) System.out.println(">> FINISHED DEPTH: " + iter);
    }

    private static ArrayList<ArrayList<Integer>> MakePaths(ArrayList<ArrayList<Integer>> maze, boolean verbose) {
        ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < maze.size(); i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for (int z = 0; z < maze.get(0).size(); z++){
                temp.add(0);
            }
            paths.add(temp);
        }
        int start_y = -1;
        int start_x = -1;
        int end_y = -1;
        int end_x = -1;
        for (int row = 0; row < maze.size(); row++) {
            for (int col = 0; col < maze.get(0).size(); col++) {
                int value = maze.get(row).get(col);
                if (value == 1) {
                    start_y = row;
                    start_x = col;
                }else if (value == 2){
                    end_y = row;
                    end_x = col;
                }
            }
            if (start_y != -1 && start_x != -1 && end_y != -1 && end_x != -1) {
                break;
            }
        }
        
        paths.get(start_y).set(start_x, 1);
        System.out.println(String.format("Start at [%d, %d], Finish at [%d, %d]", start_x, start_y, end_x, end_y));
        NumberAdjacent(maze, paths, start_y, start_x, 2, verbose);
        // System.out.println("Paths:");
        // printMaze(paths);
        var way = findPath(paths, start_y, start_x, end_y, end_x, verbose);

        return way;

    }

    private void Backtrack() {

    }

    private static ArrayList<ArrayList<Integer>> findPath(ArrayList<ArrayList<Integer>> path, int x_start, int y_start, int x_finish, int y_finish, boolean verbose) {
        ArrayList<ArrayList<Integer>> backPath = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int z = 0; z < path.get(0).size(); z++){
                temp.add(0);
            }
            backPath.add(temp);
        }

        backPath.get(x_start).set(y_start, -1);

        boolean finished = false;
        int temp_x = x_finish;
        int temp_y = y_finish;
        
        int[][] x_y = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        if (verbose) System.out.println(String.format("[%d, %d] << %d", temp_x, temp_y, path.get(temp_x).get(temp_y)));
        while(!finished) {
            int value = path.get(temp_x).get(temp_y);
            for (var mod : x_y){
                int x = temp_x + mod[0];
                int y = temp_y + mod[1];
                try {
                    int next = path.get(x).get(y);
                    if (verbose) System.out.print(String.format("[%d, %d] %d --> [%d, %d] %d \n", x, y, value, temp_x, temp_y, next));
                    if (next == 1){
                        finished = true;
                        break;
                    }
                    if(next + 1 == value){
                        if (verbose) System.out.println(String.format("moving to [%d, %d]", x, y));
                        backPath.get(x).set(y, 1);
                        temp_x = x;
                        temp_y = y;
                        break;
                    }
                } catch (IndexOutOfBoundsException e) {
                    if (verbose) System.out.print(String.format("(bounds [%d, %d])...\n", x, y));
                }
            }
        }

        return backPath;
    }

    private static void printMaze(ArrayList<ArrayList<Integer>> maze) {
        for (int row = 0; row < maze.size(); row++) {
            System.out.println(String.format("[%d] %s", row, Arrays.toString(maze.get(row).toArray())));
        }
    }

    private static void printSolution(ArrayList<ArrayList<Integer>> maze, ArrayList<ArrayList<Integer>> path){
        for (int x = 0; x < path.size(); x++){
            for(int y = 0; y < path.get(x).size(); y++){
                if(maze.get(x).get(y) == -1){
                    System.out.print("\u001B[34m# \u001B[0m");
                }else if(path.get(x).get(y) == 0){
                    System.out.print("  ");
                }else{
                    System.out.print("\u001B[31m+ \u001B[0m");
                }
            }
            System.out.println();
        }
    }
}
