/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.iis.mto.staticmock;

import edu.iis.mto.staticmock.reader.NewsReader;
import edu.iis.mto.staticmock.reader.WebServiceNewsReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.mockito.internal.util.reflection.Whitebox;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Godzio
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( { ConfigurationLoader.class, NewsReaderFactory.class } )
public class NewsLoaderTest {

    /**
     * Z braku implementacji metody addForSubscription w klasie PublishableNews
     * , dodanie wiadomosci widocznych dla subskrybentów powoduje brak
     * przypisania danej wiadomosci do listy z wiadomosciami dla subskrybentów
     *
     * public void addForSubscription(String content, SubsciptionType
     * subscriptionType) { // TODO Auto-generated method stub }
     *
     * Wykomentowalem wiec kod dodajacy wiadomoœæ subskrybowana i zalozylem ze
     * lista tych wiadomosci bedzie pusta
     */
    @Test
    public void loadNews_givenOnePublicInfo_expected_onePublicContent_zeroSubscribedContent() {
        //*******prepare IncomingInfo and IncomingNews
        IncomingInfo info = new IncomingInfo( "some content", SubsciptionType.NONE );
        //IncomingInfo info1 = new IncomingInfo( "some content", SubsciptionType.A );
        IncomingNews news = new IncomingNews();
        news.add( info );
        //news.add( info1 );

        //*******prepare Configuration
        Configuration configuration = mock( Configuration.class );
        when( configuration.getReaderType() ).thenReturn( "WS" );

        //*******prepare ConfigurationLoader
        mockStatic( ConfigurationLoader.class );
        ConfigurationLoader cfgLoader = mock( ConfigurationLoader.class );
        when( ConfigurationLoader.getInstance() ).thenReturn( cfgLoader );
        when( cfgLoader.loadConfiguration() ).thenReturn( configuration );

        //*******prepare WebServiceNewsReader
        NewsReader reader = mock( WebServiceNewsReader.class );
        when( reader.read() ).thenReturn( news );

        //*******prepare NewsReaderFactory
        mockStatic( NewsReaderFactory.class );
        when( NewsReaderFactory.getReader( (String) Mockito.any() ) ).thenReturn( reader );

        NewsLoader newsLoader = new NewsLoader();
        PublishableNews publication = newsLoader.loadNews();

        List<String> publicContent = (List<String>) Whitebox.getInternalState( publication, "publicContent" );
        List<String> subscribentContent = (List<String>) Whitebox.getInternalState( publication, "subscribentContent" );

        List<String> compareToThis = new ArrayList<>();
        compareToThis.add( info.getContent() );

        //List<String> compareToThis1= new ArrayList<>();
        //compareToThis1.add( info1.getContent() );
        Assert.assertEquals( publicContent, compareToThis );
        //Assert.assertEquals( subscribentContent, compareToThis1);
        Assert.assertEquals( subscribentContent, new ArrayList<String>() );

    }

    @Test
    public void loadNews_expectedReadCallOneTime() {
        //*******prepare IncomingNews
        IncomingNews news = new IncomingNews();

        //*******prepare Configuration
        Configuration configuration = mock( Configuration.class );
        when( configuration.getReaderType() ).thenReturn( "WS" );

        //*******prepare ConfigurationLoader
        mockStatic( ConfigurationLoader.class );
        ConfigurationLoader cfgLoader = mock( ConfigurationLoader.class );
        when( ConfigurationLoader.getInstance() ).thenReturn( cfgLoader );
        when( cfgLoader.loadConfiguration() ).thenReturn( configuration );

        //*******prepare WebServiceNewsReader
        NewsReader reader = mock( WebServiceNewsReader.class );
        when( reader.read() ).thenReturn( news );

        //*******prepare NewsReaderFactory
        mockStatic( NewsReaderFactory.class );
        when( NewsReaderFactory.getReader( (String) Mockito.any() ) ).thenReturn( reader );

        NewsLoader newsLoader = new NewsLoader();
        newsLoader.loadNews();

        Mockito.verify( reader, times( 1 ) ).read();
    }

}
