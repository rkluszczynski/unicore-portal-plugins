package pl.plgrid.unicore.common.ui.model;

/**
 * Created by Rafal on 2014-04-06.
 */
public interface GridInputFileComponent {

    public enum GridInputFileValueType {
        VALUE_CONTENT,
        VALUE_GRID_PATH
    }

    public GridInputFileData getInputFileData();


    public class GridInputFileData {
        private GridInputFileValueType type;
        private String data;

        public GridInputFileData(GridInputFileValueType type, String data) {
            this.type = type;
            this.data = data;
        }

        public GridInputFileValueType getType() {
            return type;
        }

        public void setType(GridInputFileValueType type) {
            this.type = type;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
