package me.trysam.extremewands.util;

import me.trysam.extremewands.ExtremeWandsPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookWriter {

    private File textFile;
    private String bookTitle;
    private String bookAuthor;
    private List<String> bookPages = new ArrayList<>();
    private ExtremeWandsPlugin plugin;

    public BookWriter(File textFile, String bookTitle, String bookAuthor, ExtremeWandsPlugin plugin) {
        this.textFile = textFile;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.plugin = plugin;
        loadText();
    }

    public ItemStack generateBook() {
        ItemStack stack = new ItemBuilder(Material.WRITTEN_BOOK).setName("§6Ancient Knowledge").build();
        BookMeta meta = (BookMeta) stack.getItemMeta();
        meta.setGeneration(BookMeta.Generation.TATTERED);
        meta.setAuthor(bookAuthor);
        meta.setTitle(bookTitle);
        meta.setPages(bookPages);
        meta.getPersistentDataContainer().set(plugin.getNamespaces().getBook().getIsAncientKnowledgeBook(), PersistentDataType.BYTE, (byte)1);
        stack.setItemMeta(meta);
        return stack;
    }

    private void loadText() {
        try {
            Scanner sc = new Scanner(textFile);
            StringBuilder builder = new StringBuilder();
            while (sc.hasNextLine()) {
                builder.append(sc.nextLine()+System.lineSeparator());
            }
            int charCount = 0;
            StringBuilder page = new StringBuilder();
            char[] text = builder.toString().toCharArray();
            for (int i = 0; i < text.length; i++) {
                char c = text[i];
                if (charCount > 230 ) {
                    if (c == ' ' || (i > 0 && c == 'n' && text[i-1] == '\\')) {
                        page.append(c);
                        bookPages.add(page.toString().replace("\\n", "\n"));
                        page = new StringBuilder();
                        charCount = 0;
                        continue;
                    }else {
                        page.append(c);
                        if (charCount > 250) {
                            bookPages.add(page.toString());
                            page = new StringBuilder();
                            charCount = 0;
                            continue;
                        }
                    }
                } else {
                    page.append(c);
                }
                charCount++;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public List<String> getBookPages() {
        return bookPages;
    }
}
