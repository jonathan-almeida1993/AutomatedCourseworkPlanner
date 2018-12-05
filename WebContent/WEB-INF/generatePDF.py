# -*- coding: utf-8 -*-
import requests
import shutil
import socket
import json

HOST = '127.0.0.1'  # Standard loopback interface address (localhost)
PORT = 65432        # Port to listen on (non-privileged ports are > 1023)

jsonstr = ""
file = open('log1.txt','w')
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with conn:
        print('Connected by', addr)
        while True:
            data = conn.recv(100)
            strdata = data.decode("utf-8")
            jsonstr = jsonstr + strdata
            if strdata.endswith('@@@'):
                break
      
    
        jsonstr = jsonstr[:-3]
#        print("FINAL LENGTH ",len(jsonstr))    
#        print("FINAL STRING: "+jsonstr)    
        file.write(jsonstr)
        file.write('\n')
        file.write('Generating PDF..\n')
        
        url  = 'https://www.pdfotter.com/api/v1/pdf_templates/tem_G2r8QYm5bHroPf/fill'
        auth = ('test_o4PWUBbYZ87K45jrKGswbQCFvYe4vNt3', '')
        
        file.write('posting request...')
        response = requests.post(url, auth=auth, data=json.loads(jsonstr), stream=True)
        file.write('writing to file...')
        if response.status_code == 200:
            with open('result.pdf', 'wb') as file1:
                shutil.copyfileobj(response.raw, file1)
        
        conn.sendall(b'done')
        file.close()
    
#def genPDF():
#    url  = 'https://www.pdfotter.com/api/v1/pdf_templates/tem_G2r8QYm5bHroPf/fill'
#    auth = ('test_o4PWUBbYZ87K45jrKGswbQCFvYe4vNt3', '')
#    file.write('posting request...')
#    response = requests.post(url, auth=auth, data=json.loads(jsonstr), stream=True)
#    file.write('writing to file...')
#    if response.status_code == 200:
#        with open('result.pdf', 'wb') as file1:
#            shutil.copyfileobj(response.raw, file1)
            