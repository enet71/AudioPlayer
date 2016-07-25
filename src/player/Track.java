package player;

public class Track {
    private String name;
    private String time;
    private String path;

    public Track(String name, String time, String path) {
        this.name = name;
        this.time = time;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (name != null ? !name.equals(track.name) : track.name != null) return false;
        if (time != null ? !time.equals(track.time) : track.time != null) return false;
        return !(path != null ? !path.equals(track.path) : track.path != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Track{" +
                "path='" + path + '\'' +
                ", time='" + time + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
