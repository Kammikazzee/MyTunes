package mytunes.gui.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javafx.stage.Stage;
import javafx.util.Callback;
import mytunes.be.Playlist;
import mytunes.be.PlaylistItem;
import mytunes.be.Song;
import mytunes.gui.model.MusicPlayer;
import mytunes.gui.model.PlaylistItemModel;
import mytunes.gui.model.PlaylistModel;
import mytunes.gui.model.SongModel;
import mytunes.gui.util.AlertDisplayer;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Controller of main view window (sample.fxml)
 * It enables the user to choose CRUD operations on both songs
 * and playlists. Also it provides the functionality of playing songs
 * and searching them based on a title
 *
 * @author Kjell&& && Kamila &&Kuba
 * */

public class Controller implements Initializable {

    //Tables and Lists
    public TableView<Playlist> playlistsTable;
    public TableView<PlaylistItem> playlistItemTableView;
    public TableView<Song> songsTable;
    public ListView<Song> songsOnPlaylistView;
    //used for the searching functionality
    public TextField searchBar;



    // Music Player
    private MusicPlayer musicPlayer;
    @FXML private Slider volumeSlider;
    @FXML private Text nowPlaying;
    @FXML private Text nowPlayingArtist;
    @FXML private Text nowPlayingPlaylist;
    private Song song;
    public ImageView mainImage;


    //TableView Columns Playlists
    @FXML
    private TableColumn<Playlist, String> columnName;
    @FXML
    private TableColumn<Playlist, Integer> columnSong;
    @FXML
    private TableColumn<Playlist, String> columnTime;

    //TableView Columns Songs
    @FXML private TableColumn<Song, ImageView> columnImage;
    @FXML private  TableColumn<Song, String> columnTitle;
    @FXML private  TableColumn<Song, String> columnArtist;
    @FXML private TableColumn<Song, String > columnCategory;
    @FXML TableColumn<Song, String> columnTimeSong;

    //instances of models
    private PlaylistModel playlistModel;
    private PlaylistItemModel playlistItemModel;
    private SongModel songModel;
    private AlertDisplayer alertDisplayer;
  

    private boolean filterButton;

    public Controller() {
        songModel = SongModel.createOrGetInstance();
        playlistModel = PlaylistModel.createOrGetInstance();
        alertDisplayer = new AlertDisplayer();
        musicPlayer = new MusicPlayer();
        song = musicPlayer.getSong();
        playlistItemModel = PlaylistItemModel.createOrGetInstance();
    }

    /**
     * method is called then insance Controller is created
     * it is used to prepare the TableViews and set initial values
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setObservableTableSongs(songModel);
        setObservableTablePlaylists(playlistModel);

        //I don't remember what it means
        filterButton = true;

        // Music player

        songsTable.getItems().addListener(new ListChangeListener<Song>() {
            @Override
            public void onChanged(Change<? extends Song> change) {
                musicPlayer.setSongList(songsTable.getItems());
            }
        });

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
             @Override
             public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                 musicPlayer.setVolume(volumeSlider.getValue());
             }
         });

        playlistsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                if (newSelection.getSongs() == null) songsOnPlaylistView.getItems().clear();
                else songsOnPlaylistView.setItems(FXCollections.observableArrayList(newSelection.getSongs()));
            }
        });

        mainImage.setImage(new Image("/Images/default.png"));
        loadSongsFromThePlaylists();

        songsOnPlaylistView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    musicPlayer.setSongList(songsOnPlaylistView.getItems());
                    nowPlayingPlaylist.setText("playing from "+playlistsTable.getSelectionModel().getSelectedItem().getName());
                    try {
                        musicPlayer.loadMedia(songsOnPlaylistView.getSelectionModel().getSelectedItem());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    musicPlayer.setVolume(volumeSlider.getValue());
                    musicPlayer.play();
                    updateSongInfo(musicPlayer.getSong());
                }
            }
        });

        songsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    musicPlayer.setSongList(songsTable.getItems());
                    nowPlayingPlaylist.setText("");
                    try {
                        musicPlayer.loadMedia(songsTable.getSelectionModel().getSelectedItem());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    musicPlayer.setVolume(volumeSlider.getValue());
                    musicPlayer.play();
                    updateSongInfo(musicPlayer.getSong());
                }
            }
        });
    }

    /**
     * loads into the memory the songs that are in each playlist
     */
    private void loadSongsFromThePlaylists() {
        //load the hashmap or whaever we have for that songs

    }

    /**
     * method sets the TableView Songs so that whenever change happen
     * in Songs table in Database it is visible for the user
     * @param songModel
     */
    private void setObservableTableSongs(SongModel songModel) {
        //Initialize TableView Songs
        columnImage.setCellValueFactory(new PropertyValueFactory<Song, ImageView>("image"));
        columnTitle.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
        columnArtist.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));


        columnCategory.setCellValueFactory(new PropertyValueFactory<Song, String>("category"));
        columnTimeSong.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Song, String> p) {
                return new ReadOnlyObjectWrapper(songModel.convertToString(  p.getValue().getPlaytime()));
            }
        });
        songModel.load();
        songsTable.setItems(songModel.getAllSongs());
    }

    /**
         method sets the TableView Playlists so that whenever change happen
     * in Playlists table in Database it is visible for the user
     * @param playlistModel
     */
    private void setObservableTablePlaylists(PlaylistModel playlistModel)
    {

        //Initialize TableView Playlists
        columnName.setCellValueFactory(new PropertyValueFactory<Playlist, String>("name"));
        columnSong.setCellValueFactory(new PropertyValueFactory<Playlist, Integer>("numberOfSongs"));
        //columnTime.setCellValueFactory(new PropertyValueFactory<Playlist, Integer>("totalPlaytime"));

        columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Playlist, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Playlist, String> p) {
                return new ReadOnlyObjectWrapper(songModel.convertToString(  p.getValue().getTotalPlaytime()));
            }
        });

        playlistModel.load();
        playlistsTable.setItems(playlistModel.getAllPlaylists());
    }


    public void createPlaylist(ActionEvent actionEvent) {
        openCreateOrEditPlaylistWindow(null);
    }

    public void editPlaylist(ActionEvent actionEvent) {
        Playlist playlist = playlistsTable.getSelectionModel().getSelectedItem();
        if(playlist==null)
            alertDisplayer.displayInformationAlert("Playlist", "No playlist selected",
                    "Select playlist");
        else
            openCreateOrEditPlaylistWindow(playlistsTable.getSelectionModel().getSelectedItem());
    }

    private void openCreateOrEditPlaylistWindow(Playlist selectedItem)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/myTunes/gui/view/editPlaylist.fxml"));
        Parent root =null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //EditPlaylistController editPlaylistController = loader.getController();
        //editPlaylistController.sendPlaylist(playlistsTable.getSelectionModel().getSelectedItem());
        EditPlaylistController editPlaylistController = loader.getController();
        if (selectedItem!=null)
        {editPlaylistController.sendPlaylist(selectedItem);
        //playlistModel.updatePlaylist();
        }
        else
            editPlaylistController.sendPlaylist(null);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("create/edit playlist");
        stage.show();
    }

    public void deletePlaylist(ActionEvent actionEvent) {
        playlistItemModel.safePlaylistDelete(playlistsTable.getSelectionModel().getSelectedItem());
        playlistModel.deletePlaylist(playlistsTable.getSelectionModel().getSelectedItem());
    }

    public void searchAction(ActionEvent actionEvent) {
        if(filterButton) {
            songsTable.setItems(songModel.searchSongs(searchBar.getText()));
            filterButton = false;
        } else {
            songsTable.setItems(songModel.getAllSongs());
            searchBar.clear();
            filterButton = true;
        }
    }


    /**
     *method opens a new window when button new is pressed.
     * When selectedItem is null it is the signal in the later part of the
     * code that user wants to add new song. In the opposite case it indicates that
     * user wants to edit selected song from the TableView
     */
    @FXML
    private void openAddWindow() {
        loadOpenAddSongWindow(null);
    }

    /**
     * method at first gets the selected item from the TableView
     * and then assures that selected item is null
     * if yes: it shows a prompt with information for the user
     * if no(isn't null): it executes the request
     * @param event
     */
    public void openEditSongButton(ActionEvent event) {
        Song song = songsTable.getSelectionModel().getSelectedItem();
        if (song==null)
            alertDisplayer.displayInformationAlert("Song",
                    "song isn't selected", "please choose song");

        else if(song != null)
            loadOpenAddSongWindow(song);
    }


    /**
     * Window opened when user creates or updates a song
     * it opens a new window and sends the necessary data to other controller
     * @param selectedItem
     */
    private void loadOpenAddSongWindow(Song selectedItem)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mytunes/gui/view/editSong.fxml"));
        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditSongController editSongController= loader.getController();
        //Create a file chooser object which then will be send to the EditSongController
        FileChooser fileChooser = new FileChooser();
        editSongController.setModel(songModel, fileChooser, selectedItem);

        Stage stage = new Stage();
        stage.setTitle("New/Edit Song");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Method opens confirmation window that is used to ensure
     * that user wants to delete a song
     * it also sends the selected item to the newly opened window
     */
    public void deleteSongButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mytunes/gui/view/deleteSongPrompt.fxml"));
        Parent root = loader.load();
        DeleteSongPrompt deleteSongPrompt = loader.getController();
        //send the song to another controller
        deleteSongPrompt.getSong(songsTable.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Prompt");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Method used to sort the ListView containing Playlists.
     * It sorts alfabeticaly at the top is A and at the bottom is Z
     * @param event
     */
    public void sortAscending(ActionEvent event) {
        //Playlist playlistSelected = playlistsTable.getSelectionModel().getSelectedItem();
         List<Song> unsortedList = songsOnPlaylistView.getItems();

        Comparator<Song> compareByTitle = new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
            }
        };

        Collections.sort(unsortedList, compareByTitle);

       songsOnPlaylistView.setItems(FXCollections.observableList(unsortedList));


    }

    /**
     * Method used to sort the ListView.
     * It sorts in reversed order. at the top is Z and at the bottom is A
     * @param event
     */
    public void sortDescending(ActionEvent event) {
        List<Song> unsortedList = songsOnPlaylistView.getItems();

        Comparator<Song> compareByTitle = new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {

                return o2.getTitle().toLowerCase().compareTo(o1.getTitle().toLowerCase());
            }
        };
        Collections.sort(unsortedList, compareByTitle);
        songsOnPlaylistView.setItems(FXCollections.observableList(unsortedList));
    }


    public void addSongToPlaylist(ActionEvent actionEvent) {
        try {
            Song songSelected = songsTable.getSelectionModel().getSelectedItem();
            Playlist playlistSelected = playlistsTable.getSelectionModel().getSelectedItem();
            addingNewSongToPlaylist(playlistSelected, songSelected);
            songsOnPlaylistView.setItems(FXCollections.observableList(playlistSelected.getSongs()));
        } catch (Exception e) {
            System.out.println("No song or playlist selected");
            e.printStackTrace();
        }
    }

    /**
     * Method will be called whenever we will add a new song to PlaylistView
     * it will update a properties number of songs and playtime in TableView Playlist
     */
    private void addingNewSongToPlaylist(Playlist playlist, Song song)
    {
        //updating DB
        playlistModel.updateTotalTimeOnPlaylistADD(playlist, song.getPlaytime());
        playlistModel.incrementNumberOfSongsOnPlaylist(playlist);
        //update observable list of Playlsits in playlistmodel
        playlistModel.addSongToPlaylist(playlist, song);
        //force TableView Playlists to refresh
        playlistModel.load();
    }


    /**
     * Method will be called whenever we remove a song from a PlaylistView
     * it will update a properties number of songs and playtime in TableView Playlist
     */
    public void btnDeleteSongsFromPlaylist(ActionEvent event) {
        Song songSelected = songsOnPlaylistView.getSelectionModel().getSelectedItem();
        Playlist playlistSelected = playlistsTable.getSelectionModel().getSelectedItem();
        playlistItemModel.deletePlaylistItem(playlistSelected, songSelected);

        //updating DB
        playlistModel.updateTotalTimeOnPlaylistRemove(playlistSelected, songSelected.getPlaytime());
        playlistModel.decrementNumberOfSongsOnPlaylist(playlistSelected);

        //force TableView Playlists to refresh
        playlistModel.load();
        songsOnPlaylistView.setItems(FXCollections.observableList(playlistSelected.getSongs()));
    }

    public void btnClose(ActionEvent event) {
        System.exit(0);
    }

    public void play(ActionEvent actionEvent) throws MalformedURLException {
        if(!musicPlayer.isPaused()){
            musicPlayer.pause();
        } else if(musicPlayer.isPaused()) {
            musicPlayer.setVolume(volumeSlider.getValue());
            musicPlayer.play();
        }


    }

    public void previousSong(ActionEvent actionEvent) throws MalformedURLException {
        Song s = musicPlayer.getPreviousSongInList();

        musicPlayer.pause();
        musicPlayer.loadMedia(s);
        songsTable.getSelectionModel().select(s);

        musicPlayer.setVolume(volumeSlider.getValue());
        updateSongInfo(s);
        musicPlayer.play();
    }

    public void nextSong(ActionEvent actionEvent) throws MalformedURLException {
        Song s = musicPlayer.getNextSongInList();

        musicPlayer.pause();
        musicPlayer.loadMedia(s);
        songsTable.getSelectionModel().select(s);

        updateSongInfo(s);
        musicPlayer.setVolume(volumeSlider.getValue());
        musicPlayer.play();
    }

    private void updateSongInfo(Song song) {
        nowPlaying.setText(song.getTitle());
        nowPlayingArtist.setText(song.getArtist());
        mainImage.setImage(new Image(song.getImagePath().replace("src", "")));
    }
}