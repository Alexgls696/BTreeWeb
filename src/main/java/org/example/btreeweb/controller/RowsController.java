package org.example.btreeweb.controller;

import lombok.RequiredArgsConstructor;
import org.example.btreeweb.exception.NoSuchKeyException;
import org.example.btreeweb.service.RowsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/rows")
public class RowsController {

    private final RowsService rowsService;

    @GetMapping("/all")
    public Iterable<String> getRows() {
        return rowsService.findAll();
    }

    @PostMapping("/contains")
    public boolean findIfExists(@RequestBody Map<String, String> params) {
        return rowsService.contains(params.get("row"));
    }

    @GetMapping("/first-last")
    public Iterable<String>getFirstLast() {
        return rowsService.findFirstAndLast();
    }

    @PostMapping("/between")
    public Iterable<String> getRowsBetween(@RequestBody Map<String, String> params) {
        return rowsService.findBetween(params.get("str1"), params.get("str2"));
    }

    @PostMapping("/equal-length")
    public Iterable<String> findIfEquals(@RequestBody Map<String, String> params) {
        return rowsService.findIfEqualLength(params.get("row"));
    }

    @PostMapping("/less-than")
    public Iterable<String> findIfLessThan(@RequestBody Map<String, String> params) {
        return rowsService.findIfLessThan(params.get("row"));
    }

    @PostMapping("/more-than")
    public Iterable<String> findIfMoreThan(@RequestBody Map<String, String> params) {
        return rowsService.findIfMoreThan(params.get("row"));
    }

    @PostMapping("/add")
    public ResponseEntity<?> insert(@RequestBody Map<String, String> params) {
        try {
            rowsService.add(params.get("row"));
            return ResponseEntity.ok().body(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> params) throws NoSuchKeyException {
        if(rowsService.contains(params.get("row"))){
            rowsService.remove(params.get("row"));
            return ResponseEntity
                    .ok()
                    .body(Map.of("status", "success"));
        }
        String value = "Ключ %s не найден".formatted(params.get("row"));
        throw new NoSuchKeyException(value);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String,String>> clear() {
        rowsService.clear();
        return ResponseEntity
                .ok(Map.of("result","Дерево очищено"));
    }

    private List<String>getRowsFromFile(MultipartFile file) throws IOException {
        List<String> rows = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            String line = "";
            while ((line = reader.readLine()) != null) {
                rows.add(line);
            }
        }catch (IOException e){
            throw e;
        }
        return rows;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<Integer> uploadFile(@RequestParam("file") MultipartFile file) throws IOException{
        var rows = getRowsFromFile(file);
        rowsService.clear();
        rows.forEach(rowsService::add);
        return ResponseEntity
                .ok()
                .body(rows.size());
    }
}
