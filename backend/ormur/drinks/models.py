# -*- coding: utf-8 -*-
from __future__ import unicode_literals

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

	def __str__(self):
		return self.title