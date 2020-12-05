package mytunes.dal;

import mytunes.be.Playlist;

import mytunes.be.Song;
import mytunes.dal.exception.DALexception;

import mytunes.be.PlaylistItem;

import mytunes.dal.interfaces.IPlaylistItemRepository;

import mytunes.dal.interfaces.IPlaylistRepository;
import mytunes.dal.interfaces.ISongRepository;

import java.util.List;

public class DALcontroller implements IDALFacade {

    private IPlaylistRepository playlistAccess;
    private ISongRepository songAccess;
    private IPlaylistItemRepository playlistItemAccess;

    private IPlaylistItemRepository itemAccess;



    public DALcontroller() {
        playlistAccess = new PlaylistDAO();
        songAccess = new SongDAO();
        playlistItemAccess = new PlaylistItemDAO();


    }

    //PlaylistDAO

    @Override
    public List<Playlist> getAllPlaylists() throws DALexception {
        return  playlistAccess.getAllPlaylists();
    }

    @Override
    public Playlist createPlaylist(String playName) throws DALexception {
       return playlistAccess.createPlaylist(playName);
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws DALexception {
        playlistAccess.deletePlaylist(playlist);
    }

    @Override
    public void updatePlaylistName(Playlist playlist, String newPlaylistName) throws DALexception {
        playlistAccess.updatePlaylistName(playlist, newPlaylistName);
    }

    //songDAO
    @Override
    public List<Song> getAllSongs() throws DALexception {
        return  songAccess.getAllSongs();
    }

    @Override
    public Song createSong(String title, String artist, String category, String filePath) throws DALexception {
        return  songAccess.createSong(title, artist, category, filePath );
    }

    @Override
    public void deleteSong(Song song) throws DALexception {
        songAccess.deleteSong(song);

    }

    @Override
    public void updateSong(Song song, String title, String artist, String category, String filePath) throws DALexception {
        songAccess.updateSong(song, title, artist, category, filePath);
    }

    @Override
    public Song getSong(int id) throws DALexception {
       return songAccess.getSong(id);
    }

    //PlaylistItemDAO
    @Override
    public List<PlaylistItem> getAllPlaylistItems() throws DALexception {
        return playlistItemAccess.getAllPlaylistItems();
    }

    @Override
    public PlaylistItem createPlaylistItem(int songId, int playlistId) throws DALexception {
        return playlistItemAccess.createPlaylistItem(songId, playlistId);
    }

    @Override
    public void deleteSong(PlaylistItem playlistItem) throws DALexception {
            playlistItemAccess.deleteSong(playlistItem);
    }

}
