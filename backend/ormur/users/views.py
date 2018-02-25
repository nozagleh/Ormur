# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import uuid

from django.shortcuts import render
from django.http import JsonResponse

from users.models import User

# Create your views here.
def addUser(self):
	user = User(key=str(uuid.uuid4()))
	user.save()

	return JsonResponse({'type':'user','key':user.key})