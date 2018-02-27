# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import json
from django.utils import timezone
from django.db import models

# Create your models here.
class Drink(models.Model):
	title = models.CharField(max_length=64)
	description = models.TextField(default='')
	rating = models.FloatField()
	location = models.CharField(max_length=100)
	added = models.DateTimeField(auto_now_add=True)
	lastmodified = models.DateTimeField(auto_now=True)
	user = models.ForeignKey(
		'users.User',
		on_delete=models.CASCADE
	)

	def __str__(self):
		return self.title

	def getJSON(self):
		return {'drink':{'id':self.id, 'title':self.title, 'rating':self.rating, 'description':self.description, 'location':self.location}}